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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.util.PlayerLookup;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static vc.data.dto.Tables.*;

@Tags({@Tag(name = "Deaths")})
@RestController
public class DeathsController {
    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public DeathsController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    public record Death(
        OffsetDateTime time,
        String deathMessage,
        String victimPlayerName,
        UUID victimPlayerUuid,
        String killerPlayerName,
        UUID killerPlayerUuid,
        String weaponName,
        String killerMob
    ) {}
    public record DeathsResponse(List<Death> deaths, int total, int pageCount) {}
    public record KillsResponse(List<Death> kills, int total, int pageCount) {}
    public record PlayerDeathOrKillCountResponse(List<PlayerDeathOrKillCount> players) {}
    public record PlayerDeathOrKillCount(UUID uuid, String playerName, int count) {}

    @GetMapping("/deaths")
    @RateLimiter(name = "main")
    @Cacheable("deaths")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Death history for given player",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DeathsResponse.class)
                )
            }
        ),
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
    public ResponseEntity<DeathsResponse> deaths(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "playerName", required = false) String playerName,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "page", required = false) Integer page) {
        if (pageSize != null && pageSize > 100) {
            return ResponseEntity.badRequest().build();
        }
        if (uuid == null && playerName == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UUID> optionalResolvedUuid = playerLookup.getOrResolveUuid(uuid, playerName);
        if (optionalResolvedUuid.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        final UUID resolvedUuid = optionalResolvedUuid.get();
        final int size = pageSize == null ? 25 : pageSize;
        var baseQuery = dsl
            .selectFrom(DEATHS)
            .where(DEATHS.VICTIM_PLAYER_UUID.eq(resolvedUuid));
        if (startDate != null) {
            baseQuery = baseQuery.and(DEATHS.TIME.greaterOrEqual(startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()));
        }
        if (endDate != null) {
            baseQuery = baseQuery.and(DEATHS.TIME.lessOrEqual(endDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()));
        }
        Long rowCount = dsl
            .selectCount()
            .from(baseQuery)
            .fetchOneInto(Long.class);
        if (rowCount == null) rowCount = 0L;
        var offset = (page == null ? 0 : Math.max(0, page - 1)) * size;
        List<Death> deathsList = dsl
            .selectFrom(DEATHS)
                .where(DEATHS.VICTIM_PLAYER_UUID.eq(resolvedUuid))
                .orderBy(DEATHS.TIME.desc())
                .limit(size)
                .offset(offset)
                .fetch()
                .into(Death.class);
        if (deathsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new DeathsResponse(deathsList, rowCount.intValue(), (int) Math.ceil(rowCount / (double) size)));
        }
    }

    @GetMapping("/kills")
    @RateLimiter(name = "main")
    @Cacheable("kills")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Kill history for given player",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = KillsResponse.class)
                )
            }
        ),
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
    public ResponseEntity<KillsResponse> kills(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "playerName", required = false) String playerName,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "page", required = false) Integer page) {
        if (pageSize != null && pageSize > 100) {
            return ResponseEntity.badRequest().build();
        }
        if (uuid == null && playerName == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UUID> optionalResolvedUuid = playerLookup.getOrResolveUuid(uuid, playerName);
        if (optionalResolvedUuid.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        final UUID resolvedUuid = optionalResolvedUuid.get();
        final int size = pageSize == null ? 25 : pageSize;
        var baseQuery = dsl
            .selectFrom(DEATHS)
            .where(DEATHS.KILLER_PLAYER_UUID.eq(resolvedUuid));
        if (startDate != null) {
            baseQuery = baseQuery.and(DEATHS.TIME.greaterOrEqual(startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()));
        }
        if (endDate != null) {
            baseQuery = baseQuery.and(DEATHS.TIME.lessOrEqual(endDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()));
        }
        Long rowCount = dsl
            .selectCount()
            .from(baseQuery)
            .fetchOneInto(Long.class);
        if (rowCount == null) rowCount = 0L;
        var offset = (page == null ? 0 : Math.max(0, page - 1)) * size;
        List<Death> deathsList = dsl
            .selectFrom(DEATHS)
                .where(DEATHS.KILLER_PLAYER_UUID.eq(resolvedUuid))
                .orderBy(DEATHS.TIME.desc())
                .limit(size)
                .offset(offset)
                .fetch()
                .into(Death.class);
        if (deathsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new KillsResponse(deathsList, rowCount.intValue(), (int) Math.ceil(rowCount / (double) size)));
        }
    }

    @GetMapping("/deaths/top/month")
    @RateLimiter(name = "main")
    @Cacheable("deathsTopMonth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Top deaths for the month",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PlayerDeathOrKillCountResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data for the month",
            content = @Content
        )
    })
    public ResponseEntity<PlayerDeathOrKillCountResponse> deathsTopMonth() {
        List<PlayerDeathOrKillCount> players = dsl
            .selectFrom(TOP_DEATHS_MONTH_VIEW)
            .fetch()
            .map(r -> new PlayerDeathOrKillCount(
                r.getVictimPlayerUuid(),
                r.getVictimPlayerName(),
                r.getDeathCount().intValue()));
        if (players.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new PlayerDeathOrKillCountResponse(players));
        }
    }

    @GetMapping("/kills/top/month")
    @RateLimiter(name = "main")
    @Cacheable("killsTopMonth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Top kills for the month",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PlayerDeathOrKillCountResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data for the month",
            content = @Content
        )
    })
    public ResponseEntity<PlayerDeathOrKillCountResponse> killsTopMonth() {
        List<PlayerDeathOrKillCount> players = dsl
            .selectFrom(TOP_KILLS_MONTH_VIEW)
            .fetch()
            .map(r -> new PlayerDeathOrKillCount(
                r.getKillerPlayerUuid(),
                r.getKillerPlayerName(),
                r.getKillCount().intValue()));
        if (players.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new PlayerDeathOrKillCountResponse(players));
        }
    }
}
