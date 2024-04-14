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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.util.PlayerLookup;

import java.time.OffsetDateTime;
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

    public record ChatsResponse(List<Chat> chats, int total, int pageCount) { }
    public record Chat(OffsetDateTime time, String chat) {}

    @GetMapping("/chats")
    @RateLimiter(name = "main")
    @Cacheable("chats")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Chat history for given player",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatsResponse.class)
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
        var baseQuery = dsl.select(CHATS.TIME, CHATS.CHAT)
            .from(CHATS)
            .where(CHATS.PLAYER_UUID.eq(resolvedUuid));
        Long rowCount = dsl.selectCount()
                .from(baseQuery)
                .fetchOneInto(Long.class);
        if (rowCount == null) rowCount = 0L;
        var offset = (page == null ? 0 : Math.max(0, page - 1)) * size;
        List<Chat> chats = baseQuery
                .orderBy(CHATS.TIME.desc())
                .limit(size)
                .offset(offset)
                .fetch()
                .into(Chat.class);
        if (chats.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new ChatsResponse(chats, rowCount.intValue(), (int) Math.ceil(rowCount / (double) size)));
        }
    }
}
