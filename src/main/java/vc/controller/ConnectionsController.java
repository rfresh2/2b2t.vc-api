package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Parameter;
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
import vc.data.dto.enums.Connectiontype;
import vc.util.PlayerLookup;
import vc.util.Sort;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static vc.data.dto.tables.Connections.CONNECTIONS;

@Tags({@Tag(name = "Connections")})
@RestController
public class ConnectionsController {
    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public ConnectionsController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    public record ConnectionsResponse(List<Connection> connections, int total, int pageCount) { }
    public record Connection(OffsetDateTime time, Connectiontype connection) {}
    public record PlayerConnection(OffsetDateTime time, Connectiontype connection, String playerName, UUID uuid) {}
    public record ConnectionsWindowResponse(List<PlayerConnection> connections) {}

    @GetMapping("/connections")
    @RateLimiter(name = "main")
    @Cacheable("connections")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = """
            Connection history for given player.
            
            Results are sorted in descending order by default.
            
            startDate and endDate must be ISO 8601 formatted strings, in this format: yyyy-MM-dd
            
            Example: "2022-10-31"
            """,
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ConnectionsResponse.class)
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
    public ResponseEntity<ConnectionsResponse> connections(
            @RequestParam(value = "uuid", required = false) UUID uuid,
            @Parameter(description = "Resolves to current UUID") @RequestParam(value = "playerName", required = false) String playerName,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "sort", required = false) Sort sort,
            @Parameter(description = "Must be between 1-100") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Parameter(description = "Response page") @RequestParam(value = "page", required = false) Integer page) {
        if (pageSize != null && pageSize > 100) {
            return ResponseEntity.badRequest().build();
        }
        if (uuid == null && playerName == null) {
            return ResponseEntity.badRequest().build();
        }
        if (sort == null) sort = Sort.DESC;
        Optional<UUID> optionalResolvedUuid = playerLookup.getOrResolveUuid(uuid, playerName);
        if (optionalResolvedUuid.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        final UUID resolvedUuid = optionalResolvedUuid.get();
        final int size = pageSize == null ? 25 : pageSize;
        if (startDate == null) {
            startDate = LocalDate.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (endDate.equals(startDate) || endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().build();
        }
        var baseQuery = dsl
            .selectFrom(CONNECTIONS)
            .where(CONNECTIONS.PLAYER_UUID.eq(resolvedUuid)
                .and(CONNECTIONS.TIME.greaterOrEqual(startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()))
                .and(CONNECTIONS.TIME.lessOrEqual(endDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()))
            );
        Long rowCount = dsl
            .selectCount()
            .from(baseQuery)
            .fetchOneInto(Long.class);
        if (rowCount == null) {
            return ResponseEntity.noContent().build();
        }
        var offset = (page == null ? 0 : Math.max(0, page - 1)) * size;
        List<Connection> connections = baseQuery
            .orderBy(CONNECTIONS.TIME.sort(sort.toJooq()))
            .limit(size)
            .offset(offset)
            .fetch()
            .map(c -> new Connection(c.getTime(), c.getConnection()));
        if (connections.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new ConnectionsResponse(connections, rowCount.intValue(), (int) Math.ceil(rowCount / (double) size)));
        }
    }

    @GetMapping("/connections/window")
    @RateLimiter(name = "main")
    @Cacheable("connectionsWindow")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = """
            All 2b2t connections during a window of time, starting from startDate until endDate or pageSize is met
            
            Results are sorted in ascending order by default.

            startDate and endDate must be ISO 8601 formatted strings, in this format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX
            
            Example: "2022-10-31T01:30:00.000".
            """,
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ConnectionsWindowResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No connections found in this window.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content
        )
    })
    public ResponseEntity connectionsWindow(
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        @RequestParam(value = "sort", required = false) Sort sort,
        @Parameter(description = "Must be between 1-100") @RequestParam(value = "pageSize", required = false) Integer pageSize,
        @Parameter(description = "Response page") @RequestParam(value = "page", required = false) Integer page
    ) {
        if (pageSize != null && pageSize > 100) {
            return ResponseEntity.badRequest().build();
        }
        if (sort == null) sort = Sort.ASC;
        final int size = pageSize == null ? 25 : pageSize;
        if (startDate == null) {
            startDate = LocalDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        if (endDate.equals(startDate) || endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().build();
        }
        var offset = (page == null ? 0 : Math.max(0, page - 1)) * size;
        List<PlayerConnection> connections = dsl.selectFrom(CONNECTIONS)
            .where(CONNECTIONS.TIME.greaterOrEqual(startDate.atOffset(ZoneOffset.UTC))
                .and(CONNECTIONS.TIME.lessOrEqual(endDate.atOffset(ZoneOffset.UTC))))
            .orderBy(CONNECTIONS.TIME.sort(sort.toJooq()))
            .limit(size)
            .offset(offset)
            .fetch()
            .map(c -> new PlayerConnection(c.getTime(), c.getConnection(), c.getPlayerName(), c.getPlayerUuid()));
        if (connections.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new ConnectionsWindowResponse(connections));
        }
    }
}
