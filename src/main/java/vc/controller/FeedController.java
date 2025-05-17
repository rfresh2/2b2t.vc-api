package vc.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vc.feed.LiveChat;
import vc.feed.LiveConnections;
import vc.feed.LiveDeaths;
import vc.feed.LiveFeed;
import vc.feed.dto.ChatsFeedRecord;
import vc.feed.dto.ConnectionsFeedRecord;
import vc.feed.dto.DeathsFeedRecord;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Tags({@Tag(name = "Feed")})
@RestController
public class FeedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedController.class);
    private final FeedHandler<ChatsFeedRecord> chatFeed;
    private final FeedHandler<DeathsFeedRecord> deathsFeed;
    private final FeedHandler<ConnectionsFeedRecord> connectionsFeed;

    public FeedController(
        final LiveChat liveChat,
        final LiveDeaths liveDeaths,
        final LiveConnections liveConnections
    ) {
        this.chatFeed = new FeedHandler<>("Chat", liveChat);
        this.deathsFeed = new FeedHandler<>("Deaths", liveDeaths);
        this.connectionsFeed = new FeedHandler<>("Connections", liveConnections);
    }

    @GetMapping("/feed/chats")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Server Sent Events (SSE) stream of live 2b2t chat.",
            content = {
                @Content(
                    mediaType = "text/event-stream",
                    schema = @Schema(
                        implementation = ChatsFeedRecord.class,
                        contentMediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                )
            }
        )
    })
    public SseEmitter chatFeedSSE() {
        return chatFeed.addEmitter();
    }

    @GetMapping("/feed/deaths")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Server Sent Events (SSE) stream of live 2b2t death messages.",
            content = {
                @Content(
                    mediaType = "text/event-stream",
                    schema = @Schema(
                        implementation = DeathsFeedRecord.class,
                        contentMediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                )
            }
        )
    })
    public SseEmitter deathsFeedSSE() {
        return deathsFeed.addEmitter();
    }

    @GetMapping("/feed/connections")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Server Sent Events (SSE) stream of live 2b2t connections.",
            content = {
                @Content(
                    mediaType = "text/event-stream",
                    schema = @Schema(
                        implementation = ConnectionsFeedRecord.class,
                        contentMediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                )
            }
        )
    })
    public SseEmitter connectionsFeedSSE() {
        return connectionsFeed.addEmitter();
    }

    public record FeedHandler<MessageType>(
        String id,
        LiveFeed<MessageType> liveFeed,
        Map<String, SseEmitter> emitters,
        Function<MessageType, Object> messageMapper
    ) {
        public FeedHandler(String id, LiveFeed<MessageType> liveFeed) {
            this(id, liveFeed, new ConcurrentHashMap<>(), m -> m);
        }

        public FeedHandler(String id, LiveFeed<MessageType> liveFeed, Function<MessageType, Object> messageMapper) {
            this(id, liveFeed, new ConcurrentHashMap<>(), messageMapper);
        }

        public FeedHandler {
            liveFeed.registerMessageConsumer(this::messageConsumer);
        }

        private void messageConsumer(MessageType m) {
            var msg = messageMapper.apply(m);
            for (var emitter : emitters.values()) {
                try {
                    emitter.send(msg, MediaType.APPLICATION_JSON);
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }
        }

        public SseEmitter addEmitter() {
            var emitter = new SseEmitter();
            var emitterId = UUID.randomUUID().toString();
            emitter.onCompletion(() -> removeEmitter(emitterId));
            emitter.onTimeout(() -> removeEmitter(emitterId));
            emitters.put(emitterId, emitter);
            LOGGER.info("Added {} emitter: {}", id, emitters.size());
            return emitter;
        }

        private void removeEmitter(String emitterId) {
            if (emitters.remove(emitterId) != null) {
                LOGGER.info("Removed {} emitter: {}", id, emitters.size());
            }
        }
    }
}
