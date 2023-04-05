package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.Deaths;

import java.util.List;
import java.util.UUID;

@Tags({@Tag(name = "Deaths")})
@RestController
public class DeathsController {
    private final DSLContext dsl;

    public DeathsController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/deaths")
    @RateLimiter(name = "main")
    @Cacheable("deaths")
    public List<vc.data.dto.tables.pojos.Deaths> deaths(@RequestParam(value = "uuid") UUID uuid, @RequestParam(value = "page", required = false) Integer page) {
        return dsl.selectFrom(vc.data.dto.tables.Deaths.DEATHS)
                .where(Deaths.DEATHS.VICTIM_PLAYER_UUID.eq(uuid))
                .orderBy(vc.data.dto.tables.Deaths.DEATHS.TIME.desc())
                .limit(100)
                .offset(page == null ? 0 : page * 100)
                .fetch()
                .into(vc.data.dto.tables.pojos.Deaths.class);
    }

    @GetMapping("/kills")
    @RateLimiter(name = "main")
    @Cacheable("kills")
    public List<vc.data.dto.tables.pojos.Deaths> kills(@RequestParam(value = "uuid") UUID uuid, @RequestParam(value = "page", required = false) Integer page) {
        return dsl.selectFrom(vc.data.dto.tables.Deaths.DEATHS)
                .where(Deaths.DEATHS.KILLER_PLAYER_UUID.eq(uuid))
                .orderBy(vc.data.dto.tables.Deaths.DEATHS.TIME.desc())
                .limit(100)
                .offset(page == null ? 0 : page * 100)
                .fetch()
                .into(vc.data.dto.tables.pojos.Deaths.class);
    }
}
