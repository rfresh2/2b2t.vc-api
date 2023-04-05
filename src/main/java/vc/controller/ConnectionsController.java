package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.pojos.Connections;

import java.util.List;
import java.util.UUID;

@RestController
public class ConnectionsController {
    private final DSLContext dsl;

    public ConnectionsController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/connections")
    @RateLimiter(name = "main")
    @Cacheable("connections")
    public List<Connections> connections(@RequestParam(value = "uuid") UUID uuid, @RequestParam(value = "page", required = false) Integer page) {
        return dsl.selectFrom(vc.data.dto.tables.Connections.CONNECTIONS)
                .where(vc.data.dto.tables.Connections.CONNECTIONS.PLAYER_UUID.eq(uuid))
                .orderBy(vc.data.dto.tables.Connections.CONNECTIONS.TIME.desc())
                .limit(100)
                .offset(page == null ? 0 : page * 100)
                .fetch()
                .into(vc.data.dto.tables.pojos.Connections.class);
    }
}
