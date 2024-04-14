package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Name history for given player",
                content = {
                    @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = Names.class))
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
    public ResponseEntity<List<Names>> names(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @RequestParam(value = "playerName", required = false) String playerName) {
        if (uuid == null && playerName == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UUID> optionalPlayerUUID = playerLookup.getOrResolveUuid(uuid, playerName);
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
