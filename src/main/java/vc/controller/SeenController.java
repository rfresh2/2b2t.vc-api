package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vc.data.dto.tables.Connections;
import vc.data.dto.tables.records.ConnectionsRecord;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class SeenController {

    private final DSLContext dsl;

    public SeenController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/seen")
    @RateLimiter(name = "main")
    @Cacheable("seen")
    public SeenResponse seen(@RequestParam(value = "uuid") UUID uuid) {
        Connections c = Connections.CONNECTIONS;
        ConnectionsRecord connectionsRecord = dsl.selectFrom(c)
                .where(c.PLAYER_UUID.eq(uuid))
                .orderBy(c.TIME.desc())
                .limit(1)
                .fetchOne();
        if (connectionsRecord != null) {
            return new SeenResponse(connectionsRecord.get(c.TIME));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
    }

    public static class SeenResponse {
        private final OffsetDateTime time;

        public SeenResponse(final OffsetDateTime time) {
            this.time = time;
        }

        public OffsetDateTime getTime() {
            return time;
        }
    }

    @GetMapping("/firstSeen")
    @RateLimiter(name = "main")
    @Cacheable("firstSeen")
    public SeenResponse firstSeen(@RequestParam(value = "uuid") UUID uuid) {
        Connections c = Connections.CONNECTIONS;
        ConnectionsRecord connectionsRecord = dsl.selectFrom(c)
                .where(c.PLAYER_UUID.eq(uuid))
                .orderBy(c.TIME.asc())
                .limit(1)
                .fetchOne();
        if (connectionsRecord != null) {
            return new SeenResponse(connectionsRecord.get(c.TIME));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
    }
}
