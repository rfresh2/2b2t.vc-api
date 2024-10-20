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

import static vc.data.dto.tables.Tablist.TABLIST;

@Tags({@Tag(name = "TabList")})
@RestController
public class TabListController {
    private final DSLContext dsl;

    public TabListController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public record TablistResponse(List<TablistEntry> players) { }
    public record TablistEntry(String playerName, UUID playerUuid) { }

    @GetMapping("/tablist")
    @RateLimiter(name = "main")
    @Cacheable("tablist")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of online players",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TablistResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data",
            content = @Content
        )
    })
    public ResponseEntity<TablistResponse> onlinePlayers() {
        List<TablistEntry> players = dsl.selectFrom(TABLIST)
            .fetch()
            .stream()
            .map(t -> new TablistEntry(t.getPlayerName(), t.getPlayerUuid()))
            .toList();
        if (players.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(new TablistResponse(players));
    }
}
