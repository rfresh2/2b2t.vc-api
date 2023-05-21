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
import vc.data.dto.tables.pojos.Names;
import vc.util.PlayerLookup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tags({@Tag(name = "Names")})
@RestController
public class NamesController {

    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public NamesController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    @GetMapping("/names")
    @RateLimiter(name = "main")
    @Cacheable("names")
    public ResponseEntity<List<Names>> names(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "username", required = false) String username) {
        if (uuid == null && username == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UUID> optionalPlayerUUID = playerLookup.getOrResolveUuid(uuid, username);
        if (optionalPlayerUUID.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        final UUID resolvedUuid = optionalPlayerUUID.get();
        vc.data.dto.tables.Names n = vc.data.dto.tables.Names.NAMES;
        List<Names> namesList = dsl.selectFrom(n)
                .where(n.UUID.eq(resolvedUuid))
                .orderBy(n.CHANGEDTOAT.desc().nullsLast())
                .fetch()
                .into(Names.class);
        if (namesList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(namesList);
        }
    }

}
