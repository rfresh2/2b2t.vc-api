package vc.feed;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vc.feed.dto.ChatsFeedRecord;

import java.util.concurrent.ExecutorService;

@Component
public class LiveChat extends LiveFeed<ChatsFeedRecord> {
    protected LiveChat(
        @Qualifier("virtualThreadExecutor") final ExecutorService executor,
        final RedisClient redisClient
    ) {
        super(executor, redisClient);
    }

    @Override
    MessageProcessor<ChatsFeedRecord> messageProcessor() {
        return new MessageProcessor<>(
            "ChatsTopic",
            ChatsFeedRecord.class
        );
    }
}
