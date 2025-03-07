package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.Routines;
import vc.util.PlayerLookup;

import java.util.UUID;

@Tags({@Tag(name ="Data Dump")})
@RestController
public class DataDumpController {
    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public DataDumpController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    @RateLimiter(name = "main")
    @GetMapping("/dump/player")
    @Cacheable("playerDataDump")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Data dump for given player",
            content = {
                @Content(
                    mediaType = "application/csv"
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
    public ResponseEntity<String> getPlayerDataDump(
        @RequestParam(value = "uuid", required = false) UUID uuid,
        @RequestParam(value = "playerName", required = false) String playerName
    ) {
        if (uuid == null && playerName == null)
            return ResponseEntity.badRequest().build();
        var optionalPlayerIdentity = playerLookup.getOrResolvePlayerIdentity(uuid, playerName);
        if (optionalPlayerIdentity.isEmpty())
            return ResponseEntity.noContent().build();
        var resolvedIdentity = optionalPlayerIdentity.get();
        var uuidData = Routines.getUuidData(dsl.configuration(), resolvedIdentity.uuid());
        if (uuidData.isEmpty())
            return ResponseEntity.noContent().build();
        else {
            return ResponseEntity.ok()
                .header(
                    "Content-Disposition",
                    "attachment; filename=\"" + resolvedIdentity.name() + ".csv\"")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(uuidData.formatCSV());
        }
    }
}
