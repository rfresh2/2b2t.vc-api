package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.Queuelength;

@Tags({@Tag(name = "Queue")})
@RestController
public class QueueController {
    private final DSLContext dsl;

    public QueueController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/queue")
    @RateLimiter(name = "main")
    @Cacheable("queue")
    public ResponseEntity<vc.data.dto.tables.pojos.Queuelength> queue() {
        Queuelength q = Queuelength.QUEUELENGTH;
        vc.data.dto.tables.pojos.Queuelength queuelength = dsl.selectFrom(q)
                .orderBy(q.TIME.desc().nullsLast())
                .limit(1)
                .fetchOne()
                .into(vc.data.dto.tables.pojos.Queuelength.class);
        if (queuelength != null) {
            return new ResponseEntity<>(queuelength, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
