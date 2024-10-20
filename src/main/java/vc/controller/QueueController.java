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

import java.time.OffsetDateTime;
import java.util.List;

import static vc.data.dto.tables.Queuelength.QUEUELENGTH;

@Tags({@Tag(name = "Queue")})
@RestController
public class QueueController {
    private final DSLContext dsl;

    public QueueController(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public record QueueData(OffsetDateTime time, int prio, int regular) {}
    public record QueueLengthHistory(List<QueueData> queueData) {}
    public record QueueEtaEquation(double factor, double pow) {
        public static final QueueEtaEquation INSTANCE = new QueueEtaEquation(
            199.0, 0.838
        );
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
                    schema = @Schema(implementation = QueueData.class)
                )
            }
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No data",
            content = @Content
        )
    })
    public ResponseEntity<QueueData> queue() {
        var queueData = dsl
            .selectFrom(QUEUELENGTH)
            .orderBy(QUEUELENGTH.TIME.desc().nullsLast())
            .limit(1)
            .fetchOne()
            .into(QueueData.class);
        if (queueData != null) {
            return ResponseEntity.ok(queueData);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/queue/month")
    @RateLimiter(name = "main")
    @Cacheable("queue-month")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Queue length history for the last month",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = QueueLengthHistory.class)
                )
            }
        )
    })
    public ResponseEntity<QueueLengthHistory> queueHistory() {
        var queueDataList = dsl
            .selectFrom(QUEUELENGTH)
            .where(QUEUELENGTH.TIME.greaterOrEqual(OffsetDateTime.now().minusMonths(1)))
            .orderBy(QUEUELENGTH.TIME.desc().nullsLast())
            .fetch()
            .into(QueueData.class);
        return ResponseEntity.ok(new QueueLengthHistory(queueDataList));
    }

    @GetMapping("/queue/eta-equation")
    @RateLimiter(name = "queue-eta-equation")
    @Cacheable("queue-eta-equation")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "ETA seconds = factor * (queuePosition ^ pow)",
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
}
