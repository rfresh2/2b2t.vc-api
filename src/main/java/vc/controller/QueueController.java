package vc.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
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
    @RateLimiter(name = "queue")
    @Cacheable("queue")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Current queue length",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = vc.data.dto.tables.pojos.Queuelength.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data",
            content = @Content
        )
    })
    public ResponseEntity<vc.data.dto.tables.pojos.Queuelength> queue() {
        Queuelength q = Queuelength.QUEUELENGTH;
        vc.data.dto.tables.pojos.Queuelength queuelength = dsl.selectFrom(q)
                .orderBy(q.TIME.desc().nullsLast())
                .limit(1)
                .fetchOne()
                .into(vc.data.dto.tables.pojos.Queuelength.class);
        if (queuelength != null) {
            return ResponseEntity.ok(queuelength);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/queue/eta-equation")
    @RateLimiter(name = "queue-eta-equation")
    @Cacheable("queue-eta-equation")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "ETA seconds = constant * (queuePosition ^ pow)",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = QueueEtaEquation.class)
                )
            }
        )
    })
    public ResponseEntity<QueueEtaEquation> etaEquation() {
        return ResponseEntity.ok(QueueEtaEquation.INSTANCE);
    }

    public record QueueEtaEquation(double constant, double pow) {
        public static QueueEtaEquation INSTANCE = new QueueEtaEquation(
            343.0,
            0.743);
    }
}
