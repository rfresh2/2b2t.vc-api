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

import static vc.data.dto.Tables.TABLIST_INFO;
import static vc.data.dto.Tables.TABLIST_TEXT;
import static vc.data.dto.tables.Tablist.TABLIST;

@Tags({@Tag(name = "TabList")})
@RestController
public class TabListController {
    private final DSLContext dsl;

    public TabListController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public record TablistResponse(List<TablistEntry> players, String header) { }
    public record TablistEntry(String playerName, UUID uuid) { }
    public record TablistInfoResponse(List<TablistInfoEntry> players, String header, int count, int prioCount, int nonPrioCount, int botCount) { }
    public record TablistInfoEntry(String playerName, UUID uuid, boolean prio, boolean bot) { }

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
            .sorted((a, b) -> a.playerName().compareToIgnoreCase(b.playerName()))
            .toList();
        if (players.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        String header = dsl.select(TABLIST_TEXT.HEADER_TEXT)
            .from(TABLIST_TEXT)
            .orderBy(TABLIST_TEXT.ID.desc())
            .limit(1)
            .fetch()
            .getValue(0, TABLIST_TEXT.HEADER_TEXT);
        return ResponseEntity.ok(new TablistResponse(players, header));
    }

    @GetMapping("/tablist/info")
    @RateLimiter(name = "main")
    @Cacheable("tablistInfo")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of online players with additional info about their prio and bot status",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TablistInfoResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data",
            content = @Content
        )
    })
    public ResponseEntity<TablistInfoResponse> onlinePlayersInfo() {
        List<TablistInfoEntry> players = dsl.selectFrom(TABLIST_INFO)
            .fetch()
            .stream()
            .map(t -> new TablistInfoEntry(t.getPlayerName(), t.getPlayerUuid(), t.getPrio(), t.getIsBot()))
            .sorted((a, b) -> a.playerName().compareToIgnoreCase(b.playerName()))
            .toList();
        if (players.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        int prioCount, nonPrioCount, botCount;
        prioCount = nonPrioCount = botCount = 0;
        for (int i = 0; i < players.size(); i++) {
            TablistInfoEntry player = players.get(i);
            if (player.prio()) {
                prioCount++;
            } else {
                nonPrioCount++;
            }
            if (player.bot()) {
                botCount++;
            }
        }
        String header = dsl.select(TABLIST_TEXT.HEADER_TEXT)
            .from(TABLIST_TEXT)
            .orderBy(TABLIST_TEXT.ID.desc())
            .limit(1)
            .fetch()
            .getValue(0, TABLIST_TEXT.HEADER_TEXT);
        return ResponseEntity.ok(new TablistInfoResponse(players, header, players.size(), prioCount, nonPrioCount, botCount));
    }
}
