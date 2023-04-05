package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.Queuelength;

@RestController
public class QueueController {
    private final DSLContext dsl;

    public QueueController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/queue")
    @RateLimiter(name = "main")
    @Cacheable("queue")
    public vc.data.dto.tables.pojos.Queuelength queue() {
        Queuelength q = Queuelength.QUEUELENGTH;
        return dsl.selectFrom(q)
                .orderBy(q.TIME.desc().nullsLast())
                .limit(1)
                .fetchOne()
                .into(vc.data.dto.tables.pojos.Queuelength.class);
    }
}
