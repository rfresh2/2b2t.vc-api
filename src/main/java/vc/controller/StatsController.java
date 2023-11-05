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

    @RateLimiter(name = "main")
    @GetMapping("/stats/player")
    @Cacheable("playerStats")
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

    public record PlayerStats(
        int joinCount,
        int leaveCount,
        int deathCount,
        int killCount,
        OffsetDateTime firstSeen,
        OffsetDateTime lastSeen,
        int playtimeSeconds,
        int playtimeSecondsMonth,
        int chatsCount
    ) { }
}
