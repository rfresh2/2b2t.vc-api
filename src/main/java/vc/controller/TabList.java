package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.pojos.OnlinePlayers;

import java.util.List;

import static vc.data.dto.tables.OnlinePlayers.ONLINE_PLAYERS;

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
        return dsl.selectFrom(ONLINE_PLAYERS)
                .fetch()
                .into(OnlinePlayers.class);
    }
}
