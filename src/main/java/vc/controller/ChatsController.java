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

import java.util.List;
import java.util.UUID;

@Tags({@Tag(name = "Chats")})
@RestController
public class ChatsController {

    private final DSLContext dsl;

    public ChatsController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/chats")
    @RateLimiter(name = "main")
    @Cacheable("chats")
    public ResponseEntity<List<Chats>> chats(
            @RequestParam(value = "uuid") UUID uuid,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "page", required = false) Integer page) {
        if (pageSize != null && pageSize > 100) {
            return ResponseEntity.badRequest().build();
        }
        final int size = pageSize == null ? 25 : pageSize;
        List<Chats> chats = dsl.selectFrom(vc.data.dto.tables.Chats.CHATS)
                .where(vc.data.dto.tables.Chats.CHATS.PLAYER_UUID.eq(uuid))
                .orderBy(vc.data.dto.tables.Chats.CHATS.TIME.desc())
                .limit(size)
                .offset(page == null ? 0 : page * size)
                .fetch()
                .into(Chats.class);
        if (chats.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(chats);
        }
    }
}
