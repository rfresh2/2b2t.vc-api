package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.Tablist;

import java.util.List;
import java.util.UUID;

@Tags({@Tag(name = "TabList")})
@RestController
public class TabList {
    private final DSLContext dsl;

    public TabList(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/tablist")
    @RateLimiter(name = "main")
    @Cacheable("tablist")
    public ResponseEntity<List<TablistEntry>> onlinePlayers() {
        List<TablistEntry> into = dsl.selectFrom(Tablist.TABLIST)
            .fetch()
            .into(vc.data.dto.tables.pojos.Tablist.class)
            .stream()
            .map(t -> new TablistEntry(t.getPlayerName(), t.getPlayerUuid()))
            .toList();
        if (into.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(into);
    }

    public record TablistEntry(String playerName, UUID playerUuid) { }
}
