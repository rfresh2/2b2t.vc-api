package vc.feed;

import org.redisson.Redisson;
import org.redisson.api.RReliableTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisClient implements DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClient.class);
    private RedissonClient redissonClient;

    public RedisClient(@Value("${REDIS_URL}") final String redisURL, @Value("${REDIS_USERNAME}") final String redisUsername, @Value("${REDIS_PASSWORD}") final String redisPassword) {
        this.redissonClient = buildRedisClient(redisURL, redisUsername, redisPassword);
    }

    public RReliableTopic getTopic(final String topicName) {
        return redissonClient.getReliableTopic(topicName);
    }

    public RedissonClient buildRedisClient(final String redisURL, final String redisUsername, final String redisPassword) {
        Config config = new Config();
        config
            .useSingleServer()
            .setAddress(redisURL)
            .setUsername(redisUsername)
            .setPassword(redisPassword)
            .setConnectionMinimumIdleSize(1);
        config.setCodec(StringCodec.INSTANCE);
        return Redisson.create(config);
    }

    @Override
    public void destroy() {
        try {
            redissonClient.shutdown();
        } catch (Exception e) {
            LOGGER.error("Failed to shutdown Redisson client", e);
        }
    }
}
