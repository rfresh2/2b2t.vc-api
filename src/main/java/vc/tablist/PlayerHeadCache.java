package vc.tablist;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vc.api.crafthead.CraftheadRestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * Fetches and caches 8x8 player face images (with hat layer)
 */
@Component
public class PlayerHeadCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerHeadCache.class);
    private static final int HEAD_SIZE = 8;
    private final CraftheadRestClient craftheadRestClient;
    // limits concurrent requests to crafthead
    private final ExecutorService executor = Executors.newFixedThreadPool(2, Thread.ofPlatform()
        .name("player-head-", 0)
        .daemon(true)
        .factory());
    // failures are cached briefly so we don't hammer crafthead on every render while it's having issues
    private final AsyncLoadingCache<UUID, Optional<BufferedImage>> heads = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfter(Expiry.creating((UUID uuid, Optional<BufferedImage> head) -> head.isPresent()
            ? Duration.ofHours(6)
            : Duration.ofMinutes(5)))
        .executor(executor)
        .buildAsync(this::fetchHead);

    public PlayerHeadCache(final CraftheadRestClient craftheadRestClient) {
        this.craftheadRestClient = craftheadRestClient;
    }

    /**
     * Returns all heads that are cached or can be fetched within {@code maxWait}.
     * Heads still loading after that continue loading in the background for future calls.
     */
    public Map<UUID, BufferedImage> getHeads(final Collection<UUID> uuids, final Duration maxWait) {
        var futures = new HashMap<UUID, CompletableFuture<Optional<BufferedImage>>>(uuids.size());
        for (var uuid : uuids) {
            futures.put(uuid, heads.get(uuid));
        }
        try {
            CompletableFuture.allOf(futures.values().toArray(CompletableFuture[]::new))
                .get(maxWait.toMillis(), TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            LOGGER.debug("Timed out waiting for player heads, rendering with partial heads");
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (final ExecutionException e) {
            // individual failures are handled per-future below
        }
        var result = new HashMap<UUID, BufferedImage>(futures.size());
        futures.forEach((uuid, future) -> {
            if (future.isDone() && !future.isCompletedExceptionally()) {
                future.join().ifPresent(head -> result.put(uuid, head));
            }
        });
        return result;
    }

    private Optional<BufferedImage> fetchHead(final UUID uuid) {
        try {
            var bytes = craftheadRestClient.getAvatar(uuid, HEAD_SIZE);
            return Optional.ofNullable(ImageIO.read(new ByteArrayInputStream(bytes)));
        } catch (final Exception e) {
            LOGGER.debug("Failed fetching player head for {}: {}", uuid, e.getMessage());
            return Optional.empty();
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }
}
