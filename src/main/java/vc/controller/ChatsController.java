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
    public record WordCount(int count) {}
    public record PlayerChat(String playerName, UUID uuid, OffsetDateTime time, String chat) {}
    public record ChatSearchResponse(List<PlayerChat> chats, int total, int pageCount) {}

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

    @GetMapping("/chats/word-count")
    @RateLimiter(name = "main")
    @Cacheable("chats-word-count")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Counts the number of times a word has appeared in chat",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = WordCount.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request. The word must be at least 4 characters long.",
            content = @Content
        )
    })
    public ResponseEntity<WordCount> wordCount(
        @RequestParam(value = "word", required = true) String word
    ) {
        if (word == null || word.length() < 3) {
            return ResponseEntity.badRequest().build();
        }
        var count = dsl.selectCount()
            .from(CHATS)
            .where(CHATS.CHAT.likeIgnoreCase("%" + word + "%"))
            .fetchOneInto(Integer.class);
        if (count == null) count = 0;
        return ResponseEntity.ok(new WordCount(count));
    }

    @GetMapping("/chats/search")
    @RateLimiter(name = "main")
    @Cacheable("chats-search")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Find chat messages containing a specific word",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatsResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request.",
            content = @Content
        )
    })
    public ResponseEntity<ChatSearchResponse> chatSearch(
        @RequestParam(value = "word", required = true) String word,
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(value = "pageSize", required = false) Integer pageSize,
        @RequestParam(value = "page", required = false) Integer page
    ) {
        if (pageSize != null && pageSize > 100) {
            return ResponseEntity.badRequest().build();
        }
        if (word == null || word.length() < 4 || word.length() > 30) {
            return ResponseEntity.badRequest().build();
        }
        final int size = pageSize == null ? 25 : pageSize;
        var baseQuery = dsl
            .select(CHATS.PLAYER_NAME, CHATS.PLAYER_UUID, CHATS.TIME, CHATS.CHAT)
            .from(CHATS)
            .where(CHATS.CHAT.likeIgnoreCase("%" + word + "%"));
        if (startDate != null) {
            baseQuery = baseQuery.and(CHATS.TIME.greaterOrEqual(startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()));
        }
        if (endDate != null) {
            baseQuery = baseQuery.and(CHATS.TIME.lessOrEqual(endDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()));
        }
        Long rowCount = dsl.selectCount()
            .from(baseQuery)
            .fetchOneInto(Long.class);
        if (rowCount == null) rowCount = 0L;
        var offset = (page == null ? 0 : Math.max(0, page - 1)) * size;
        List<PlayerChat> chats = baseQuery
            .orderBy(CHATS.TIME.desc())
            .limit(size)
            .offset(offset)
            .fetch()
            .into(PlayerChat.class);
        if (chats.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new ChatSearchResponse(chats, rowCount.intValue(), (int) Math.ceil(rowCount / (double) size)));
        }
    }
}
