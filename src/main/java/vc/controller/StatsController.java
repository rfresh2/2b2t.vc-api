package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.Routines;
import vc.util.PlayerLookup;

import java.time.OffsetDateTime;
import java.util.UUID;

@Tags({@Tag(name = "Stats")})
@RestController
public class StatsController {

    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public StatsController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    public record PlayerStats(
        int joinCount,
        int leaveCount,
        int deathCount,
        int killCount,
        OffsetDateTime firstSeen,
        OffsetDateTime lastSeen,
        int playtimeSeconds,
        int playtimeSecondsMonth,
        int chatsCount,
        boolean prio
    ) { }

    @RateLimiter(name = "main")
    @GetMapping("/stats/player")
    @Cacheable("playerStats")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Stats for given player",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PlayerStats.class)
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
    public ResponseEntity<PlayerStats> playerStats(
        @RequestParam(value = "uuid", required = false) UUID uuid,
        @RequestParam(value = "playerName", required = false) String playerName
    ) {
        if (uuid == null && playerName == null) {
            return ResponseEntity.badRequest().build();
        }
        var optionalPlayerUUID = playerLookup.getOrResolveUuid(uuid, playerName);
        if (optionalPlayerUUID.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var resolvedUuid = optionalPlayerUUID.get();
        var playerActivity = Routines.playerStats(dsl.configuration(), resolvedUuid)
            .getFirst()
            .into(PlayerStats.class);
        return ResponseEntity.ok(playerActivity);
    }
}
