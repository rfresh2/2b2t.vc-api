package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

import static vc.data.dto.tables.Worldtime.WORLDTIME;

@Tags({@Tag(name = "Time")})
@RestController
public class TimeController {
    private final DSLContext dsl;

    public TimeController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public record TimeResponse(OffsetDateTime lastUpdated, int worldTime) {}

    @GetMapping("/time")
    @RateLimiter(name = "main")
    @Cacheable("worldtime")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = """
                    Returns the 2b2t world time in ticks (0 - 24000).
                    
                    To determine the real-time 2b2t world time, interpolate between lastUpdated and the current real world time.
                    
                    MC and real world time conversion help: https://minecraft.wiki/w/Daylight_cycle#Conversions
                    1 minecraft tick = 50 ms real world time
                    
                    Daytime starts at 6:00 (0 ticks) and night begins at 18:00 (12000 ticks)
                    """
        ),
        @ApiResponse(responseCode = "204", description = "No world time data available")
    })
    public ResponseEntity<TimeResponse> getWorldTime() {
        var record = dsl.selectFrom(WORLDTIME)
            .orderBy(WORLDTIME.TIME.desc())
            .limit(1)
            .fetchOne();
        if (record == null) {
            return ResponseEntity.noContent().build();
        }
        int hour = (int) (record.getWorldtime() % 24000L);
        return ResponseEntity.ok(new TimeResponse(record.getTime(), hour));
    }
}
