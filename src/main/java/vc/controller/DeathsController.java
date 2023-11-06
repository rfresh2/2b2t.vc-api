package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.pojos.Deaths;
import vc.util.PlayerLookup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static vc.data.dto.Tables.DEATHS;

@Tags({@Tag(name = "Deaths")})
@RestController
public class DeathsController {
    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public DeathsController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    public record DeathsResponse(List<Deaths> deaths, int total, int pageCount) {}

    @GetMapping("/deaths")
    @RateLimiter(name = "main")
    @Cacheable("deaths")
    public ResponseEntity<DeathsResponse> deaths(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "playerName", required = false) String playerName,
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
        var baseQuery = dsl.selectFrom(DEATHS)
            .where(DEATHS.VICTIM_PLAYER_UUID.eq(resolvedUuid));
        Long rowCount = dsl
            .selectCount()
            .from(baseQuery)
            .fetchOneInto(Long.class);
        if (rowCount == null) rowCount = 0L;
        List<vc.data.dto.tables.pojos.Deaths> deathsList = dsl.selectFrom(DEATHS)
                .where(DEATHS.VICTIM_PLAYER_UUID.eq(resolvedUuid))
                .orderBy(DEATHS.TIME.desc())
                .limit(size)
                .offset(page == null ? 0 : page * size)
                .fetch()
                .into(Deaths.class);
        if (deathsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new DeathsResponse(deathsList, rowCount.intValue(), (int) Math.ceil(rowCount / (double) size)));
        }
    }

    public record KillsResponse(List<Deaths> kills, int total, int pageCount) {}

    @GetMapping("/kills")
    @RateLimiter(name = "main")
    @Cacheable("kills")
    public ResponseEntity<KillsResponse> kills(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "playerName", required = false) String playerName,
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
        var baseQuery = dsl.selectFrom(DEATHS)
            .where(DEATHS.KILLER_PLAYER_UUID.eq(resolvedUuid));
        Long rowCount = dsl
            .selectCount()
            .from(baseQuery)
            .fetchOneInto(Long.class);
        if (rowCount == null) rowCount = 0L;
        List<vc.data.dto.tables.pojos.Deaths> deathsList = dsl.selectFrom(DEATHS)
                .where(DEATHS.KILLER_PLAYER_UUID.eq(resolvedUuid))
                .orderBy(DEATHS.TIME.desc())
                .limit(size)
                .offset(page == null ? 0 : page * size)
                .fetch()
                .into(Deaths.class);
        if (deathsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new KillsResponse(deathsList, rowCount.intValue(), (int) Math.ceil(rowCount / (double) size)));
        }
    }
}
