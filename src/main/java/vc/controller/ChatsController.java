package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
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
    public List<Chats> chats(@RequestParam(value = "uuid") UUID uuid, @RequestParam(value = "page", required = false) Integer page) {
        return dsl.selectFrom(vc.data.dto.tables.Chats.CHATS)
                .where(vc.data.dto.tables.Chats.CHATS.PLAYER_UUID.eq(uuid))
                .orderBy(vc.data.dto.tables.Chats.CHATS.TIME.desc())
                .limit(100)
                .offset(page == null ? 0 : page * 100)
                .fetch()
                .into(vc.data.dto.tables.pojos.Chats.class);
    }
}
