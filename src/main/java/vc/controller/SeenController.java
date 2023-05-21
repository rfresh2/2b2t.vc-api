package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vc.data.dto.tables.Connections;
import vc.data.dto.tables.records.ConnectionsRecord;
import vc.util.PlayerLookup;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Tags({@Tag(name = "Seen")})
@RestController
public class SeenController {

    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public SeenController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    @GetMapping("/seen")
    @RateLimiter(name = "main")
    @Cacheable("seen")
    public ResponseEntity<SeenResponse> seen(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "username", required = false) String username) {
        if (uuid == null && username == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UUID> optionalPlayerUUID = playerLookup.getOrResolveUuid(uuid, username);
        if (optionalPlayerUUID.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        final UUID resolvedUuid = optionalPlayerUUID.get();
        Connections c = Connections.CONNECTIONS;
        ConnectionsRecord connectionsRecord = dsl.selectFrom(c)
                .where(c.PLAYER_UUID.eq(resolvedUuid))
                .orderBy(c.TIME.desc())
                .limit(1)
                .fetchOne();
        if (connectionsRecord != null) {
            return new ResponseEntity<>(new SeenResponse(connectionsRecord.get(c.TIME)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
    public ResponseEntity<SeenResponse> firstSeen(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "username", required = false) String username) {
        if (uuid == null && username == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UUID> optionalPlayerUUID = playerLookup.getOrResolveUuid(uuid, username);
        if (optionalPlayerUUID.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        final UUID resolvedUuid = optionalPlayerUUID.get();
        Connections c = Connections.CONNECTIONS;
        ConnectionsRecord connectionsRecord = dsl.selectFrom(c)
                .where(c.PLAYER_UUID.eq(resolvedUuid))
                .orderBy(c.TIME.asc())
                .limit(1)
                .fetchOne();
        if (connectionsRecord != null) {
            return new ResponseEntity<>(new SeenResponse(connectionsRecord.get(c.TIME)), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
    }
}
