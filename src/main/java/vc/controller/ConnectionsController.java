package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.pojos.Connections;

import java.util.List;
import java.util.UUID;

@Tags({@Tag(name = "Connections")})
@RestController
public class ConnectionsController {
    private final DSLContext dsl;

    public ConnectionsController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/connections")
    @RateLimiter(name = "main")
    @Cacheable("connections")
    public ResponseEntity<List<Connections>> connections(
            @RequestParam(value = "uuid") UUID uuid,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "page", required = false) Integer page) {
        if (pageSize != null && pageSize > 100) {
            return ResponseEntity.badRequest().build();
        }
        final int size = pageSize == null ? 25 : pageSize;
        List<Connections> connections = dsl.selectFrom(vc.data.dto.tables.Connections.CONNECTIONS)
                .where(vc.data.dto.tables.Connections.CONNECTIONS.PLAYER_UUID.eq(uuid))
                .orderBy(vc.data.dto.tables.Connections.CONNECTIONS.TIME.desc())
                .limit(size)
                .offset(page == null ? 0 : page * size)
                .fetch()
                .into(Connections.class);
        if (connections.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(connections);
        }
    }
}
