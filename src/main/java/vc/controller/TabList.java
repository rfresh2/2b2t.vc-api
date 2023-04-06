package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.pojos.OnlinePlayers;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static vc.data.dto.tables.OnlinePlayers.ONLINE_PLAYERS;
import static vc.data.dto.tables.Playercount.PLAYERCOUNT;

@Tags({@Tag(name = "TabList")})
@RestController
public class TabList {
    private final DSLContext dsl;

    public TabList(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/tablist")
    @RateLimiter(name = "main")
    @Cacheable("onlinePlayers")
    public List<OnlinePlayers> onlinePlayers() {
        vc.data.dto.tables.pojos.Playercount playercountRecord = dsl.selectFrom(PLAYERCOUNT)
                .orderBy(PLAYERCOUNT.TIME.desc())
                .limit(1)
                .fetchOne()
                .into(vc.data.dto.tables.pojos.Playercount.class);
        if (playercountRecord.getTime().toInstant().isBefore(Instant.now().minus(Duration.ofMinutes(10)))) {
            return List.of();
        }
        return dsl.selectFrom(ONLINE_PLAYERS)
                .fetch()
                .into(OnlinePlayers.class);
    }
}
