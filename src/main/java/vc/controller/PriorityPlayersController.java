package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import vc.data.dto.tables.PriorityPlayersView;
import vc.util.PlayerLookup;

import java.util.List;
import java.util.UUID;

@Tags({@Tag(name = "PriorityPlayers")})
@RestController
public class PriorityPlayersController {

    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public PriorityPlayersController(DSLContext dsl, PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    public record PriorityPlayersResponse(String playerName, UUID uuid) { }

    @GetMapping("/players/priority")
    @RateLimiter(name = "main")
    @Cacheable("priorityPlayers")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Gets all players who currently have priority queue",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PriorityPlayersResponse.class))
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data",
            content = @Content
        )
    })
    public ResponseEntity<List<PriorityPlayersResponse>> priorityPlayers() {
        var response = dsl.selectFrom(PriorityPlayersView.PRIORITY_PLAYERS_VIEW)
            .fetch()
            .map(record -> new PriorityPlayersResponse(
                record.get(PriorityPlayersView.PRIORITY_PLAYERS_VIEW.PLAYER_NAME),
                record.get(PriorityPlayersView.PRIORITY_PLAYERS_VIEW.PLAYER_UUID)));
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }
}
