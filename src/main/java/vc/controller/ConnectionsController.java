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
import vc.data.dto.enums.Connectiontype;
import vc.util.PlayerLookup;

import java.time.OffsetDateTime;
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

    @GetMapping("/connections")
    @RateLimiter(name = "main")
    @Cacheable("connections")
    public ResponseEntity<ConnectionsResponse> connections(
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
        var baseQuery = dsl.select(CONNECTIONS.TIME, CONNECTIONS.CONNECTION)
            .from(CONNECTIONS)
            .where(CONNECTIONS.PLAYER_UUID.eq(resolvedUuid));
        Long rowCount = dsl
            .selectCount()
            .from(baseQuery)
            .fetchOneInto(Long.class);
        if (rowCount == null) rowCount = 0L;
        var offset = (page == null ? 0 : Math.max(0, page - 1)) * size;
        List<Connection> connections = baseQuery
                .orderBy(CONNECTIONS.TIME.desc())
                .limit(size)
                .offset(offset)
                .fetch()
                .into(Connection.class);
        if (connections.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new ConnectionsResponse(connections, rowCount.intValue(), (int) Math.ceil(rowCount / (double) size)));
        }
    }
}
