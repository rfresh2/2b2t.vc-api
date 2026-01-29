package vc.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheTtlProperties.class)
public class CacheConfig {
    private final CacheTtlProperties cacheTtlProperties;

    public CacheConfig(final CacheTtlProperties cacheTtlProperties) {
        this.cacheTtlProperties = cacheTtlProperties;
    }


    @Bean
    public CacheManager cacheManager() {
        var defaultTtl = cacheTtlProperties.getDefaultTtl();
        var defaultSpec = CaffeineSpec.parse("maximumSize=250,expireAfterWrite=" + defaultTtl.getSeconds() + "s");
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeineSpec(defaultSpec);
        cacheTtlProperties.getOverrides().forEach((id, ttl) -> {
            var customCache = Caffeine.newBuilder()
                .maximumSize(250)
                .expireAfterWrite(ttl)
                .recordStats()
                .build();
            caffeineCacheManager.registerCustomCache(id, customCache);
        });
        return caffeineCacheManager;
    }
}
