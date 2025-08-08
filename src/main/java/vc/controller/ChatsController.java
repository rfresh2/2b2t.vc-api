package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.duckdb.ChatsDuckDb;
import vc.util.MigrateToLiveFeedResponse;
import vc.util.PlayerLookup;
import vc.util.Sort;
import vc.util.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final ChatsDuckDb chatsDb;

    public ChatsController(final DSLContext dsl, final PlayerLookup playerLookup, final ChatsDuckDb chatsDb) {
        this.dsl = dsl;
        this.playerLookup = playerLookup;
        this.chatsDb = chatsDb;
    }

    public record WordCount(int count) {}
    public record PlayerChat(String playerName, UUID uuid, OffsetDateTime time, String chat) {}
    public record ChatSearchResponse(List<PlayerChat> chats, int total, int pageCount) {}
    public record ChatWindowResponse(List<PlayerChat> chats) {}

    @GetMapping("/chats")
    @RateLimiter(name = "main")
    @Cacheable("chats")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search for chat messages with optional filters",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatSearchResponse.class)
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
            description = """
              Bad request.
              
              If word is provided, it must be between 3 and 50 valid chat characters.
              
              If uuid or playerName is provided, it must be for a valid and existing account.
              
              If pageSize is provided, it must be between 1 and 100.
              
              If startDate and endDate are provided, they must be valid dates in ISO 8601 format, example: "2022-10-31".
              """,
            content = @Content
        )
    })
    public ResponseEntity<ChatSearchResponse> chats(
        @RequestParam(value = "word", required = false) String word,
        @RequestParam(value = "playerName", required = false) String playerName,
        @RequestParam(value = "uuid", required = false) UUID uuid,
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(value = "sort", required = false) Sort sort,
        @RequestParam(value = "pageSize", required = false) Integer pageSize,
        @RequestParam(value = "page", required = false) Integer page
    ) {
        if (pageSize != null && (pageSize < 1 || pageSize > 100)) {
            return ResponseEntity.badRequest().build();
        }
        if (word != null && (word.length() < 3 || word.length() > 50 || !Validator.isValidChat(word))) {
            return ResponseEntity.badRequest().build();
        }
        UUID resolvedUuid = null;
        if (uuid != null || playerName != null) {
            Optional<UUID> optionalResolvedUuid = playerLookup.getOrResolveUuid(uuid, playerName);
            if (optionalResolvedUuid.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            resolvedUuid = optionalResolvedUuid.get();
        }
        if (sort == null) sort = Sort.DESC;
        final int size = pageSize == null ? 25 : pageSize;

        var offset = (page == null ? 0 : Math.max(0, page - 1)) * size;
        ChatsDuckDb.ChatSearchResult chatSearchResult = chatsDb.chatSearch(
            word,
            resolvedUuid,
            startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime() : null,
            endDate != null ? endDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime() : null,
            sort,
            size,
            offset
        );
        if (chatSearchResult.searchResults().isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new ChatSearchResponse(chatSearchResult.searchResults(), chatSearchResult.totalCount(), (int) Math.ceil(chatSearchResult.totalCount() / (double) size)));
        }
    }

    @GetMapping("/chats/window")
    @RateLimiter(name = "main")
    @Cacheable("chatsWindow")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = """
            All 2b2t chats during a window of time, starting from startDate until endDate or pageSize is met.
            
            Results are sorted in ascending order by default.
            
            startDate is required if sort is ASC, endDate is required if sort is DESC.

            startDate and endDate must be ISO 8601 formatted strings, in this format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX
            
            Example: "2022-10-31T01:30:00.000".
            """,
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatWindowResponse.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No chats found in this window.",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request. startDate must be provided",
            content = @Content
        )
    })
    public ResponseEntity chatWindow(
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        @RequestParam(value = "sort", required = false) Sort sort,
        @RequestParam(value = "pageSize", required = false) Integer pageSize,
        @RequestParam(value = "page", required = false) Integer page
    ) {
        if (pageSize != null && pageSize > 100) {
            return ResponseEntity.badRequest().build();
        }
        if (sort == null) sort = Sort.ASC;
        final int size = pageSize == null ? 25 : pageSize;
        if (endDate != null && startDate != null) {
            if (endDate.equals(startDate) || endDate.isBefore(startDate)) {
                return ResponseEntity.badRequest().build();
            }
        }
        var scraperTimeCutoff = LocalDateTime.now().minusHours(1);
        switch (sort) {
            case ASC -> {
                if (startDate == null) {
                    return ResponseEntity.badRequest().build();
                }
                if (startDate.isAfter(scraperTimeCutoff)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MigrateToLiveFeedResponse("Migrate your scraping to /feed/chats"));
                }
            }
            case DESC -> {
                if (endDate == null) {
                    return ResponseEntity.badRequest().build();
                }
                if (endDate.isAfter(scraperTimeCutoff)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MigrateToLiveFeedResponse("Migrate your scraping to /feed/chats"));
                }
            }
        }
        Condition c = null;
        switch (sort) {
            case ASC -> {
                if (startDate == null) {
                    return ResponseEntity.badRequest().build();
                }
                c = CHATS.TIME.greaterOrEqual(startDate.atOffset(ZoneOffset.UTC));
                if (endDate != null)
                    c = c.and(CHATS.TIME.lessOrEqual(endDate.atOffset(ZoneOffset.UTC)));
            }
            case DESC -> {
                if (endDate == null) {
                    return ResponseEntity.badRequest().build();
                }
                c = CHATS.TIME.lessOrEqual(endDate.atOffset(ZoneOffset.UTC));
                if (startDate != null)
                    c = c.and(CHATS.TIME.greaterOrEqual(startDate.atOffset(ZoneOffset.UTC)));
            }
            default -> throw new IllegalStateException("Unexpected value: " + sort);
        }
        var offset = (page == null ? 0 : Math.max(0, page - 1)) * size;
        var chats = dsl.select(CHATS.PLAYER_NAME, CHATS.PLAYER_UUID, CHATS.TIME, CHATS.CHAT)
            .from(CHATS)
            .where(c)
            .orderBy(CHATS.TIME.sort(sort.toJooq()))
            .limit(size)
            .offset(offset)
            .fetch()
            .into(PlayerChat.class);
        if (chats.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new ChatWindowResponse(chats));
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
            description = "Bad request. The word must be between 4 and 30 characters.",
            content = @Content
        )
    })
    public ResponseEntity<WordCount> wordCount(
        @RequestParam(value = "word", required = true) String word
    ) {
        if (word == null || word.length() < 3 || word.length() > 50 || !Validator.isValidChat(word)) {
            return ResponseEntity.badRequest().build();
        }
        var count = chatsDb.wordCount(word);
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
                    schema = @Schema(implementation = ChatSearchResponse.class)
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
    @Hidden
    public ResponseEntity<ChatSearchResponse> chatSearch(
        @RequestParam(value = "word", required = true) String word,
        @RequestParam(value = "playerName", required = false) String playerName,
        @RequestParam(value = "uuid", required = false) UUID uuid,
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(value = "sort", required = false) Sort sort,
        @RequestParam(value = "pageSize", required = false) Integer pageSize,
        @RequestParam(value = "page", required = false) Integer page
    ) {
        return chats(word, playerName, uuid, startDate, endDate, sort, pageSize, page);
    }
}
