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

import static vc.data.dto.tables.MaxConsMonthView.MAX_CONS_MONTH_VIEW;

@Tags({@Tag(name = "Bots")})
@RestController
public class BotController {
    private final DSLContext dsl;

    public BotController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public record BotData(String playerName, UUID uuid) {}
    public record BotsMonthResponse(List<BotData> players) {}

    @GetMapping("/bots/month")
    @RateLimiter(name = "main")
    @Cacheable("botsMonth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of suspected bots",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BotsMonthResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data",
            content = @Content
        )
    })
    public ResponseEntity<BotsMonthResponse> onlinePlayers() {
        List<BotData> bots = dsl
            .selectFrom(MAX_CONS_MONTH_VIEW)
            .fetch()
            .map(r -> new BotData(r.getPName(), r.getPUuid()));
        if (bots.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new BotsMonthResponse(bots));
        }
    }
}

