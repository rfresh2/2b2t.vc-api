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
import vc.util.PlayerLookup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static vc.data.dto.tables.Playtime.PLAYTIME;
import static vc.data.dto.tables.TopPlaytimeMonthView.TOP_PLAYTIME_MONTH_VIEW;

@Tags({@Tag(name = "Playtime")})
@RestController
public class PlaytimeController {

    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public PlaytimeController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    public record PlaytimeMonthResponse(List<PlayerPlaytimeData> players) { }
    public record PlayerPlaytimeData(UUID uuid, String playerName, double playtimeDays) { }
    public record PlaytimeResponse(UUID uuid, int playtimeSeconds) { }

    @GetMapping("/playtime")
    @RateLimiter(name = "main")
    @Cacheable("playtime")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Playtime for given player",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PlaytimeResponse.class)
                )
            }),
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
    public ResponseEntity<PlaytimeResponse> playtime(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "playerName", required = false) String playerName
    ) {
        if (uuid == null && playerName == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UUID> optionalPlayerUUID = playerLookup.getOrResolveUuid(uuid, playerName);
        if (optionalPlayerUUID.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        final UUID resolvedUuid = optionalPlayerUUID.get();
        var playtimeSeconds = dsl
            .select(DSL.sum(PLAYTIME.PLAYTIME_SECONDS))
            .from(PLAYTIME)
            .where(PLAYTIME.PLAYER_UUID.eq(resolvedUuid))
            .fetchOneInto(Integer.class);
        if (playtimeSeconds == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new PlaytimeResponse(resolvedUuid, playtimeSeconds));
        }
    }

    @RateLimiter(name = "main")
    @GetMapping("/playtime/top/month")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Top playtime for the month",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PlaytimeMonthResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data for player",
            content = @Content
        )
    })
    public ResponseEntity<PlaytimeMonthResponse> playtimeTopMonth() {
        var players = dsl.selectFrom(TOP_PLAYTIME_MONTH_VIEW)
            .fetch()
            .stream()
            .map(topPlaytimeMonthViewRecord -> new PlayerPlaytimeData(
                topPlaytimeMonthViewRecord.getPlayerUuid(),
                topPlaytimeMonthViewRecord.getPlayerName(),
                secondsToDays(topPlaytimeMonthViewRecord.getPlaytimeSeconds())))
            .sorted((a, b) -> Double.compare(b.playtimeDays(), a.playtimeDays()))
            .toList();
        if (players.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new PlaytimeMonthResponse(players));
        }
    }

    public static double secondsToDays(final long seconds) {
        return seconds / 86400.0;
    }
}
