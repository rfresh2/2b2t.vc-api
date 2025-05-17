package vc.feed;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vc.feed.dto.DeathsFeedRecord;

import java.util.concurrent.ExecutorService;

@Component
public class LiveDeaths extends LiveFeed<DeathsFeedRecord> {
    protected LiveDeaths(
        @Qualifier("virtualThreadExecutor") final ExecutorService executor,
        final RedisClient redisClient
    ) {
        super(executor, redisClient);
    }

    @Override
    MessageProcessor<DeathsFeedRecord> messageProcessor() {
        return new MessageProcessor<>(
            "DeathsTopic",
            DeathsFeedRecord.class
        );
    }
}
