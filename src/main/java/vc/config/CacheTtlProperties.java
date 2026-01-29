package vc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "cache.ttl")
public class CacheTtlProperties {

    private Duration defaultTtl = Duration.ofMinutes(5);
    private Map<String, Duration> overrides = new HashMap<>();

    public Duration getDefaultTtl() {
        return defaultTtl;
    }

    public void setDefaultTtl(Duration defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    public Map<String, Duration> getOverrides() {
        return overrides;
    }

    public void setOverrides(Map<String, Duration> overrides) {
        this.overrides = overrides;
    }
}
