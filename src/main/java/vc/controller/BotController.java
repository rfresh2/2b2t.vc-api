package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.jooq.impl.QOM;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vc.data.dto.tables.pojos.MaxConsMonthView;
import vc.data.dto.tables.pojos.OnlinePlayers;
import vc.data.dto.tables.records.MaxConsMonthViewRecord;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static vc.data.dto.tables.OnlinePlayers.ONLINE_PLAYERS;
import static vc.data.dto.tables.MaxConsMonthView.MAX_CONS_MONTH_VIEW;

@Tags({@Tag(name = "Bots")})
@RestController
public class BotController {
    private final DSLContext dsl;

    public BotController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/bots/month")
    @RateLimiter(name = "main")
    @Cacheable("botsMonth")
    public ResponseEntity<List<MaxConsMonthView>> onlinePlayers() {
        List<MaxConsMonthView> bots = dsl.selectFrom(MAX_CONS_MONTH_VIEW).fetch().into(MaxConsMonthView.class);

        if (bots.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(bots, HttpStatus.OK);
        }
    }
}

