package vc.data.duckdb;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vc.controller.ChatsController;
import vc.util.Sort;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class ChatsDuckDb {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatsDuckDb.class);
    private final DuckDbInstance duckDbInstance;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ChatsDuckDb(
        final DuckDbInstance duckDbInstance,
        final @Qualifier("scheduledExecutor") ScheduledExecutorService scheduledExecutor,
        final @Value("${DUCK_DB_SYNC}") boolean duckDbSyncEnabled,
        final @Value("${DUCK_DB_SYNC_INTERVAL:3}") int duckDbSyncInterval
    ) {
        this.duckDbInstance = duckDbInstance;
        init();
        if (duckDbSyncEnabled)
            scheduledExecutor.scheduleAtFixedRate(this::syncChats, 0, duckDbSyncInterval, TimeUnit.MINUTES);
    }

    @Scheduled(fixedRate = 24, initialDelay = 24, timeUnit = TimeUnit.HOURS)
    private void refreshConnection() {
        lock.writeLock().lock();
        try {
            duckDbInstance.refreshConnectionPool();
        } catch (Exception e) {
            LOGGER.error("Error while refreshing connection", e);
        } finally {
            if (lock.writeLock().isHeldByCurrentThread()) {
                lock.writeLock().unlock();
            }
        }
    }

    private void init() {
        lock.writeLock().lock();
        try (var handle = duckDbInstance.getJdbi().open()) {
            handle.createUpdate("CREATE TABLE IF NOT EXISTS d_chats (time timestamptz, chat text, player_name text, player_uuid uuid)")
                .execute();
        } finally {
            if (lock.writeLock().isHeldByCurrentThread()) {
                lock.writeLock().unlock();
            }
        }
        LOGGER.info("ChatsDuckDb initialized");
    }

//    @Scheduled(fixedRate = 1, initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    private void syncChats() {
        LOGGER.info("Syncing Chats...");
        lock.readLock().lock();
        try (var handle = duckDbInstance.getJdbi().open()) {
            var lastSyncTime = handle.select("SELECT coalesce(max(time), '2011-01-01 00:00:00') FROM d_chats;")
                .mapTo(OffsetDateTime.class)
                .findOne()
                .orElseThrow();
            LOGGER.info("Last chat sync time: {}", lastSyncTime);
            // can't copy results to ram if the query is too big
            if (lastSyncTime.isBefore(OffsetDateTime.now().minusMinutes(15))) {
                // seems to not use postgres table indexes to speed up queries or something
                var updateCount = handle.createUpdate("INSERT INTO d_chats SELECT * FROM postgres_db.chats where postgres_db.chats.time > :syncTime;")
                    .bind("syncTime", lastSyncTime)
                    .execute();
                LOGGER.info("{} chats ddb synced", updateCount);
            } else {
                // much faster to do a ddb 'postgres_query' style for small data sizes
                // but need to do this awkward string concatenation to bind time param
                var toSyncChats = handle.createQuery("SELECT * FROM postgres_query('postgres_db', 'SELECT * FROM chats where \"time\" > ''%s''')"
                        .formatted(DateTimeFormatter.ISO_DATE_TIME.format(lastSyncTime)))
                    .map((rs, ctx) -> {
                        OffsetDateTime time = rs.getObject("time", OffsetDateTime.class);
                        String chat = rs.getString("chat");
                        String playerName = rs.getString("player_name");
                        String playerUuidStr = rs.getString("player_uuid");
                        UUID playerUuid = playerUuidStr != null ? UUID.fromString(playerUuidStr) : null;
                        return new ChatsController.PlayerChat(playerName, playerUuid, time, chat);
                    })
                    .list();
                LOGGER.info("Found {} chats to sync", toSyncChats.size());
                var batch = handle.prepareBatch("INSERT INTO d_chats VALUES (:time, :chat, :player_name, :player_uuid)");
                for (var toSyncChat : toSyncChats) {
                    batch
                        .bind("time", toSyncChat.time())
                        .bind("chat", toSyncChat.chat())
                        .bind("player_name", toSyncChat.playerName())
                        .bind("player_uuid", toSyncChat.uuid() != null ? toSyncChat.uuid().toString() : null)
                        .add();
                }
                int[] results = batch.execute();
                int updateCount = 0;
                for (int result : results) {
                    updateCount += result;
                }
                LOGGER.info("{} chats batch synced", updateCount);
            }
        } catch (Exception e) {
            LOGGER.error("Error syncing chats", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int totalChatsCount() {
        lock.readLock().lock();
        try (var handle = duckDbInstance.getJdbi().open()) {
            return handle.select("SELECT COUNT(*) FROM d_chats")
                .mapTo(Integer.class)
                .findOne()
                .orElse(0);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int wordCount(String word) {
        lock.readLock().lock();
        try (var handle = duckDbInstance.getJdbi().open()) {
            return handle.select("SELECT COUNT(*) FROM d_chats WHERE chat ILIKE :word")
                .bind("word", "%" + word + "%")
                .mapTo(Integer.class)
                .findOne()
                .orElse(0);
        } finally {
            lock.readLock().unlock();
        }
    }

    public record ChatSearchResult(
        int totalCount,
        List<ChatsController.PlayerChat> searchResults
    ) {}

    public ChatSearchResult chatSearch(
        @Nullable String word,
        @Nullable UUID uuid,
        @Nullable OffsetDateTime startDate,
        @Nullable OffsetDateTime endDate,
        @NonNull Sort sort,
        int pageSize,
        int offset
    ) {
        lock.readLock().lock();
        try (var handle = duckDbInstance.getJdbi().open()) {
            var countQuery = """
                   SELECT COUNT(*)
                   FROM d_chats
                   WHERE time >= :startDate
                   and time <= :endDate
                   """;
            if (word != null) {
                countQuery += """
                   and chat ilike :word
                   """;
            }
            if (uuid != null) {
                countQuery += """
                    and player_uuid = :uuid
                    """;
            }
            int totalCount = handle.select(countQuery)
                .bind("word", "%" + (word == null ? "" : word) + "%")
                .bind("startDate", startDate != null ? startDate : OffsetDateTime.parse("2011-01-01T00:00:00Z"))
                .bind("endDate", endDate != null ? endDate : OffsetDateTime.now())
                .bind("uuid", uuid == null ? "" : uuid.toString())
                .mapTo(Integer.class)
                .findOne()
                .orElse(0);
            var resultQuery = """
                SELECT player_name, player_uuid, time, chat
                FROM d_chats
                WHERE time >= :startDate
                and time <= :endDate
                """;
            if (word != null) {
                resultQuery += """
                and chat ilike :word
                """;
            }
            if (uuid != null) {
                resultQuery += """ 
                AND player_uuid = :uuid
                """;
            }
            resultQuery += "order by time " + sort.toValue() + "\n";
            resultQuery += """
                limit :pageSize
                offset :offset
                """;
            var results = handle.select(resultQuery)
                .bind("word", "%" + (word == null ? "" : word) + "%")
                .bind("startDate", startDate != null ? startDate : OffsetDateTime.parse("2011-01-01T00:00:00Z"))
                .bind("endDate", endDate != null ? endDate : OffsetDateTime.now())
                .bind("uuid", uuid == null ? "" : uuid.toString())
                .bind("pageSize", pageSize)
                .bind("offset", offset)
                .map((rs, ctx) -> new ChatsController.PlayerChat(
                    rs.getString("player_name"),
                    Optional.ofNullable(rs.getString("player_uuid"))
                        .map(UUID::fromString)
                        .orElse(null),
                    rs.getObject("time", OffsetDateTime.class),
                    rs.getString("chat")
                ))
                .list();
            return new ChatSearchResult(totalCount, results);
        } finally {
            lock.readLock().unlock();
        }
    }
}
