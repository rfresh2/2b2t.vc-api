package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.pojos.Names;

import java.util.List;
import java.util.UUID;

@RestController
public class NamesController {

    private final DSLContext dsl;

    public NamesController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/names")
    @RateLimiter(name = "main")
    @Cacheable("names")
    public List<Names> names(@RequestParam(value = "uuid") UUID uuid) {
        vc.data.dto.tables.Names n = vc.data.dto.tables.Names.NAMES;
        return dsl.selectFrom(n)
                .where(n.UUID.eq(uuid))
                .orderBy(n.CHANGEDTOAT.desc().nullsLast())
                .fetch()
                .into(Names.class);
    }

}
