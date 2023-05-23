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
import vc.data.dto.tables.Deaths;
import vc.util.PlayerLookup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tags({@Tag(name = "Deaths")})
@RestController
public class DeathsController {
    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public DeathsController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    @GetMapping("/deaths")
    @RateLimiter(name = "main")
    @Cacheable("deaths")
    public ResponseEntity<List<vc.data.dto.tables.pojos.Deaths>> deaths(
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
        List<vc.data.dto.tables.pojos.Deaths> deathsList = dsl.selectFrom(Deaths.DEATHS)
                .where(Deaths.DEATHS.VICTIM_PLAYER_UUID.eq(resolvedUuid))
                .orderBy(Deaths.DEATHS.TIME.desc())
                .limit(size)
                .offset(page == null ? 0 : page * size)
                .fetch()
                .into(vc.data.dto.tables.pojos.Deaths.class);
        if (deathsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(deathsList);
        }
    }

    @GetMapping("/kills")
    @RateLimiter(name = "main")
    @Cacheable("kills")
    public ResponseEntity<List<vc.data.dto.tables.pojos.Deaths>> kills(
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
        List<vc.data.dto.tables.pojos.Deaths> deathsList = dsl.selectFrom(Deaths.DEATHS)
                .where(Deaths.DEATHS.KILLER_PLAYER_UUID.eq(resolvedUuid))
                .orderBy(Deaths.DEATHS.TIME.desc())
                .limit(size)
                .offset(page == null ? 0 : page * size)
                .fetch()
                .into(vc.data.dto.tables.pojos.Deaths.class);
        if (deathsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(deathsList);
        }
    }
}
