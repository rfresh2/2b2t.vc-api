package vc.feed;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vc.feed.dto.ConnectionsFeedRecord;

import java.util.concurrent.ExecutorService;

@Component
public class LiveConnections extends LiveFeed<ConnectionsFeedRecord> {

    protected LiveConnections(
        @Qualifier("virtualThreadExecutor") final ExecutorService executor,
        final RedisClient redisClient
    ) {
        super(executor, redisClient);
    }

    @Override
    MessageProcessor<ConnectionsFeedRecord> messageProcessor() {
        return new MessageProcessor<>(
            "ConnectionsTopic",
            ConnectionsFeedRecord.class
        );
    }
}
