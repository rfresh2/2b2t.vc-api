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
import vc.data.dto.tables.pojos.Chats;
import vc.util.PlayerLookup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static vc.data.dto.Tables.CHATS;

@Tags({@Tag(name = "Chats")})
@RestController
public class ChatsController {
    private final DSLContext dsl;
    private final PlayerLookup playerLookup;

    public ChatsController(final DSLContext dsl, final PlayerLookup playerLookup) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
    }

    public record ChatsResponse(List<Chats> chats, int total, int pageCount) { }

    @GetMapping("/chats")
    @RateLimiter(name = "main")
    @Cacheable("chats")
    public ResponseEntity<ChatsResponse> chats(
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
        var baseQuery = dsl.selectFrom(CHATS)
            .where(CHATS.PLAYER_UUID.eq(resolvedUuid));
        Long rowCount = dsl.selectCount()
                .from(baseQuery)
                .fetchOneInto(Long.class);
        if (rowCount == null) rowCount = 0L;
        List<Chats> chats = baseQuery
                .orderBy(CHATS.TIME.desc())
                .limit(size)
                .offset(page == null ? 0 : page * size)
                .fetch()
                .into(Chats.class);
        if (chats.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new ChatsResponse(chats, rowCount.intValue(), (int) Math.ceil(rowCount / (double) size)));
        }
    }
}
