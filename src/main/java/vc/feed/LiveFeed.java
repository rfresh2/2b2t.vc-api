package vc.feed;


import org.redisson.api.RReliableTopic;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class LiveFeed<MessageType> implements DisposableBean {
    private final Logger LOGGER = getLogger(getClass().getSimpleName());
    protected final RedisClient redisClient;
    final ObjectMapper objectMapper;
    final MessageProcessor<MessageType> messageProcessor;
    final RReliableTopic topic;
    final List<MessageConsumer<MessageType>> messageConsumers;
    String topicListenerId;
    private final ExecutorService executor;

    protected LiveFeed(
        final ExecutorService executor,
        final RedisClient redisClient
    ) {
        this.executor = executor;
        this.redisClient = redisClient;
        this.objectMapper = new ObjectMapper();
        this.messageConsumers = new ArrayList<>();
        this.messageProcessor = messageProcessor();
        this.topic = getTopic(messageProcessor);
        this.topicListenerId = initTopicListener();
    }

    public record MessageProcessor<MessageType>(
        String topicName,
        Class<MessageType> deserializedType
    ) {}

    @FunctionalInterface
    public interface MessageConsumer<MessageType> {
        void consume(MessageType message);
    }

    abstract MessageProcessor<MessageType> messageProcessor();

    private String feedName() {
        return getClass().getSimpleName();
    }

    private RReliableTopic getTopic(MessageProcessor<MessageType> mapper) {
        return this.redisClient.getTopic(mapper.topicName());
    }

    private String initTopicListener() {
        return topic.addListener(String.class, (channel, message) -> {
            try {
                var msg = objectMapper.readValue(message, messageProcessor.deserializedType());
                executor.execute(() -> {
                    for (MessageConsumer<MessageType> consumer : messageConsumers) {
                        try {
                            consumer.consume(msg);
                        } catch (Exception e) {
                            LOGGER.error("Error broadcasting {} message: {}", feedName(), msg, e);
                        }
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to deserialize message from {} topic {}", feedName(), e.getMessage());
            }
        });
    }

    public void registerMessageConsumer(MessageConsumer<MessageType> messageConsumer) {
        this.messageConsumers.add(messageConsumer);
    }

    @Scheduled(initialDelay = 1, fixedRate = 1, timeUnit = HOURS)
    private void refreshTopicListener() {
        try {
            topic.removeListener(topicListenerId);
            topicListenerId = initTopicListener();
            LOGGER.info("{} topic listener refreshed", feedName());
        } catch (Exception e) {
            LOGGER.error("Failed to refresh {} topic listener", feedName(), e);
        }
    }

    @Override
    public void destroy() {
        try {
            topic.removeListener(topicListenerId);
        } catch (Exception e) {
            LOGGER.error("Failed to shutdown {} topic listener", feedName(), e);
        }
    }
}
