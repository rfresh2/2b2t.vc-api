package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.cache.annotation.Cacheable;
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
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "First and last time a player was seen on 2b2t",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SeenResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data for player",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request. Either uuid or playerName must be provided.",
            content = @Content
        )
    })
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
            return ResponseEntity.ok(new SeenResponse(connectionsRecord.value1(), connectionsRecord.value2()));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    public record SeenResponse(OffsetDateTime firstSeen, OffsetDateTime lastSeen) { }
}
