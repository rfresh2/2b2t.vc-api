package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.Connections;
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
            @RequestParam(value = "playerName", required = false) String playerName) {
        if (uuid == null && playerName == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UUID> optionalPlayerUUID = playerLookup.getOrResolveUuid(uuid, playerName);
        if (optionalPlayerUUID.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        final UUID resolvedUuid = optionalPlayerUUID.get();
        Connections c = Connections.CONNECTIONS;
        var connectionsRecord = dsl.select(
                DSL.min(c.TIME).as("firstSeen"),
                DSL.max(c.TIME).as("lastSeen"))
                .from(c)
                .where(c.PLAYER_UUID.eq(resolvedUuid))
                .fetchOne();
        if (connectionsRecord != null) {
            return new ResponseEntity<>(new SeenResponse(connectionsRecord.value1(), connectionsRecord.value2()), HttpStatus.OK);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    public record SeenResponse(OffsetDateTime firstSeen, OffsetDateTime lastSeen) { }
}
