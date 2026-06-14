package vc.data.duckdb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DuckDbInstanceTest {
    @TempDir
    Path tempDir;

    @Test
    void concurrentReadsUseMultipleDuckDbConnections() throws Exception {
        var duckDbPath = tempDir.resolve("concurrent-reads.db").toString();
        var duckDbInstance = new DuckDbInstance(
            "localhost",
            "5432",
            "postgres",
            "postgres",
            "postgres",
            duckDbPath,
            4,
            false
        );
        var executor = Executors.newFixedThreadPool(4);
        try {
            try (var handle = duckDbInstance.getJdbi().open()) {
                handle.execute("CREATE TABLE reads_test AS SELECT range AS value FROM range(1000)");
            }

            var ready = new CountDownLatch(4);
            var start = new CountDownLatch(1);
            var handlesReady = new CountDownLatch(4);
            var futures = new CompletableFuture[4];
            for (var i = 0; i < futures.length; i++) {
                futures[i] = CompletableFuture.supplyAsync(() -> {
                    try {
                        ready.countDown();
                        assertTrue(start.await(5, TimeUnit.SECONDS));
                        try (var handle = duckDbInstance.getJdbi().open()) {
                            var connectionId = handle.select("SELECT current_connection_id()")
                                .mapTo(Integer.class)
                                .one();
                            handlesReady.countDown();
                            assertTrue(handlesReady.await(5, TimeUnit.SECONDS));
                            handle.select("SELECT count(*) FROM reads_test")
                                .mapTo(Integer.class)
                                .one();
                            return connectionId;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);
            }

            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            CompletableFuture.allOf(futures).get(10, TimeUnit.SECONDS);

            var connectionIds = new HashSet<Integer>();
            for (var future : futures) {
                connectionIds.add((Integer) future.get());
            }
            assertEquals(4, connectionIds.size());
        } finally {
            executor.shutdownNow();
            duckDbInstance.destroy();
        }
    }
}
