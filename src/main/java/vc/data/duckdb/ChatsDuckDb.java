package vc.data.duckdb;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vc.controller.ChatsController;
import vc.util.Sort;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ChatsDuckDb {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatsDuckDb.class);
    private final DuckDbInstance duckDbInstance;

    public ChatsDuckDb(
        final DuckDbInstance duckDbInstance,
        final @Qualifier("scheduledExecutor") ScheduledExecutorService scheduledExecutor,
        final @Value("${DUCK_DB_SYNC}") boolean duckDbSyncEnabled
    ) {
        this.duckDbInstance = duckDbInstance;
        init();
        if (duckDbSyncEnabled)
            scheduledExecutor.scheduleAtFixedRate(this::syncChats, 0, 5, java.util.concurrent.TimeUnit.MINUTES);
        scheduledExecutor.scheduleAtFixedRate(this::refreshConnection, 24L, 24L, TimeUnit.HOURS);
    }

    private synchronized void refreshConnection() {
        try {
            duckDbInstance.connection.close();
            duckDbInstance.initializeConnection();
        } catch (Exception e) {
            LOGGER.error("Error while refreshing connection", e);
        }
    }

    private synchronized void init() {
        try (var handle = duckDbInstance.getJdbi().open()) {
            handle.createUpdate("CREATE TABLE IF NOT EXISTS d_chats (time timestamptz, chat text, player_name text, player_uuid uuid)")
                .execute();
        }
        LOGGER.info("ChatsDuckDb initialized");
    }

    private synchronized void syncChats() {
        LOGGER.info("Syncing Chats...");
        try (var handle = duckDbInstance.getJdbi().open()) {
            var lastSyncTime = handle.select("SELECT coalesce(max(time), '2016-06-01 00:00:00') FROM d_chats;")
                .mapTo(OffsetDateTime.class)
                .findOne()
                .orElseThrow();
            LOGGER.info("Last chat sync time: {}", lastSyncTime);
            var updateCount = handle.createUpdate("INSERT INTO d_chats SELECT * FROM postgres_db.chats where postgres_db.chats.time > :syncTime;")
                .bind("syncTime", lastSyncTime)
                .execute();
            LOGGER.info("{} chats synced", updateCount);
        } catch (Exception e) {
            LOGGER.error("Error syncing chats", e);
        }
    }

    public synchronized int totalChatsCount() {
        try (var handle = duckDbInstance.getJdbi().open()) {
            return handle.select("SELECT COUNT(*) FROM d_chats")
                .mapTo(Integer.class)
                .findOne()
                .orElse(0);
        }
    }

    public synchronized int wordCount(String word) {
        try (var handle = duckDbInstance.getJdbi().open()) {
            return handle.select("SELECT COUNT(*) FROM d_chats WHERE chat ILIKE :word")
                .bind("word", "%" + word + "%")
                .mapTo(Integer.class)
                .findOne()
                .orElse(0);
        }
    }

    public record ChatSearchResult(
        int totalCount,
        List<ChatsController.PlayerChat> searchResults
    ) {}

    public synchronized ChatSearchResult chatSearch(
        @NonNull String word,
        @Nullable UUID uuid,
        @Nullable OffsetDateTime startDate,
        @Nullable OffsetDateTime endDate,
        @NonNull Sort sort,
        int pageSize,
        int offset
    ) {
        try (var handle = duckDbInstance.getJdbi().open()) {
            var countQuery = """
                   SELECT COUNT(*)
                   FROM d_chats
                   WHERE chat ilike :word
                   and time >= :startDate
                   and time <= :endDate
                   """;
            if (uuid != null) {
                countQuery += """
                    and player_uuid = :uuid
                    """;
            }
            int totalCount = handle.select(countQuery)
                .bind("word", "%" + word + "%")
                .bind("startDate", startDate != null ? startDate : OffsetDateTime.parse("2018-01-01T00:00:00Z"))
                .bind("endDate", endDate != null ? endDate : OffsetDateTime.now())
                .bind("uuid", uuid == null ? "" : uuid.toString())
                .mapTo(Integer.class)
                .findOne()
                .orElse(0);
            var resultQuery = """
                SELECT player_name, player_uuid, time, chat
                FROM d_chats
                where chat ilike :word
                and time >= :startDate
                and time <= :endDate
                """;
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
                .bind("word", "%" + word + "%")
                .bind("startDate", startDate != null ? startDate : OffsetDateTime.parse("2018-01-01T00:00:00Z"))
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
        }
    }
}
