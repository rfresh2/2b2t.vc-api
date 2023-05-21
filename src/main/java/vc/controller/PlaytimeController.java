package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.routines.Playtime;
import vc.data.dto.tables.PlaytimeMonthView;
import vc.data.dto.tables.records.PlaytimeMonthViewRecord;
import vc.util.PlayerLookup;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Tags({@Tag(name = "Playtime")})
@RestController
public class PlaytimeController {

    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public PlaytimeController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    @GetMapping("/playtime")
    @RateLimiter(name = "main")
    @Cacheable("playtime")
    public ResponseEntity<PlaytimeResponse> playtime(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "username", required = false) String username
    ) {
        if (uuid == null && username == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UUID> optionalPlayerUUID = playerLookup.getOrResolveUuid(uuid, username);
        if (optionalPlayerUUID.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        final UUID resolvedUuid = optionalPlayerUUID.get();
        Playtime playtime = new Playtime();
        playtime.setPUuid(resolvedUuid);
        playtime.execute(dsl.configuration());
        Integer playtimeReturnValue = playtime.getReturnValue();
        if (playtimeReturnValue != null && playtimeReturnValue != 0) {
            return new ResponseEntity<>(new PlaytimeResponse(resolvedUuid, playtimeReturnValue), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @RateLimiter(name = "main")
    @GetMapping("/playtime/top/month")
    public ResponseEntity<List<PlaytimeTopMonthResponse>> playtimeTopMonth() {
        PlaytimeMonthViewRecord[] playtimeMonthViewRecords = dsl.selectFrom(PlaytimeMonthView.PLAYTIME_MONTH_VIEW)
                .fetchArray();
        List<PlaytimeTopMonthResponse> monthResponses = Arrays.stream(playtimeMonthViewRecords).toList().stream()
                .map(playtimeAllMonthRecord -> new PlaytimeTopMonthResponse(playtimeAllMonthRecord.get(PlaytimeMonthView.PLAYTIME_MONTH_VIEW.P_UUID), playtimeAllMonthRecord.get(PlaytimeMonthView.PLAYTIME_MONTH_VIEW.P_NAME), playtimeAllMonthRecord.get(PlaytimeMonthView.PLAYTIME_MONTH_VIEW.PT_DAYS).doubleValue()))
                .sorted((a, b) -> Double.compare(b.getPlaytimeDays(), a.getPlaytimeDays()))
                .collect(Collectors.toList());
        if (monthResponses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(monthResponses, HttpStatus.OK);
        }
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
