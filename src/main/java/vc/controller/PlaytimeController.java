package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vc.data.dto.routines.Playtime;
import vc.data.dto.tables.PlaytimeMonthView;
import vc.data.dto.tables.records.PlaytimeMonthViewRecord;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tags({@Tag(name = "Playtime")})
@RestController
public class PlaytimeController {

    private final DSLContext dsl;

    public PlaytimeController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/playtime")
    @RateLimiter(name = "main")
    @Cacheable("playtime")
    public PlaytimeResponse playtime(@RequestParam(value = "uuid") UUID uuid) {
        Playtime playtime = new Playtime();
        playtime.setPUuid(uuid);
        playtime.execute(dsl.configuration());
        Integer playtimeReturnValue = playtime.getReturnValue();
        if (playtimeReturnValue != null) {
            return new PlaytimeResponse(uuid, playtimeReturnValue);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
    }

    @RateLimiter(name = "main")
    @GetMapping("/playtime/top/month")
    public List<PlaytimeTopMonthResponse> playtimeTopMonth() {
        PlaytimeMonthViewRecord[] playtimeMonthViewRecords = dsl.selectFrom(PlaytimeMonthView.PLAYTIME_MONTH_VIEW)
                .fetchArray();
        return Arrays.stream(playtimeMonthViewRecords).toList().stream()
                .map(playtimeAllMonthRecord -> new PlaytimeTopMonthResponse(playtimeAllMonthRecord.get(PlaytimeMonthView.PLAYTIME_MONTH_VIEW.P_UUID), playtimeAllMonthRecord.get(PlaytimeMonthView.PLAYTIME_MONTH_VIEW.P_NAME), playtimeAllMonthRecord.get(PlaytimeMonthView.PLAYTIME_MONTH_VIEW.PT_DAYS).doubleValue()))
                .sorted((a, b) -> Double.compare(b.getPlaytimeDays(), a.getPlaytimeDays()))
                .collect(Collectors.toList());
    }

    public static class PlaytimeTopMonthResponse {
        private final UUID uuid;
        private final String username;
        private final double playtimeDays;

        public PlaytimeTopMonthResponse(final UUID uuid, final String username, final double playtimeDays) {
            this.uuid = uuid;
            this.username = username;
            this.playtimeDays = playtimeDays;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getUsername() {
            return username;
        }

        public double getPlaytimeDays() {
            return playtimeDays;
        }
    }

    public static class PlaytimeResponse {
        private final UUID uuid;
        private final int playtimeSeconds;

        public PlaytimeResponse(final UUID uuid, final int playtimeSeconds) {
            this.uuid = uuid;
            this.playtimeSeconds = playtimeSeconds;
        }

        public UUID getUuid() {
            return uuid;
        }

        public int getPlaytimeSeconds() {
            return playtimeSeconds;
        }
    }
}
