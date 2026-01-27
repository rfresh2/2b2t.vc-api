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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static vc.data.dto.tables.PriorityPlayersView.PRIORITY_PLAYERS_VIEW;

@Tags({@Tag(name = "PriorityPlayers")})
@RestController
public class PriorityPlayersController {

    private final DSLContext dsl;

    public PriorityPlayersController(DSLContext dsl) {
        this.dsl = dsl;
    }

    public record PriorityPlayersResponse(List<PriorityPlayer> players) { }
    public record PriorityPlayer(String playerName, UUID uuid) { }

    @GetMapping("/players/priority")
    @RateLimiter(name = "cached")
    @Cacheable("priorityPlayers")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = """
                Gets all players who (probably) have priority queue.
                
                This is calculated based on the number of times a player has:
                1. Disconnected and then connected within a brief time window.
                2. Session time above the non-prio limit.
                
                Data is refreshed daily, but the heuristics search over the past 30 days.
                """,
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PriorityPlayersResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data",
            content = @Content
        )
    })
    public ResponseEntity<PriorityPlayersResponse> priorityPlayers() {
        var response = dsl
            .selectFrom(PRIORITY_PLAYERS_VIEW)
            .fetch()
            .map(record -> new PriorityPlayer(
                record.get(PRIORITY_PLAYERS_VIEW.PLAYER_NAME),
                record.get(PRIORITY_PLAYERS_VIEW.PLAYER_UUID)));
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        response.sort((a, b) -> a.playerName().compareToIgnoreCase(b.playerName()));
        return ResponseEntity.ok(new PriorityPlayersResponse(response));
    }
}
