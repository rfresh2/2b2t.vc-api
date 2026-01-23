package vc.service;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vc.data.dto.tables.records.QueuewaitRecord;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static vc.data.dto.tables.Queuewait.QUEUEWAIT;

@Component
public class QueueETAService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueETAService.class);
    private static final int DATA_DAY_RANGE = 7;
    private static final long MIN_QUEUE_TIME_SECONDS = TimeUnit.HOURS.toSeconds(3);
    private static final int UPDATE_INTERVAL_HOURS = 6;

    private final DSLContext dsl;
    private double pow = 0.87;
    private final double factor = 199.0;

    public QueueETAService(final DSLContext dsl) {
        this.dsl = dsl;
    }

    @Scheduled(fixedRate = UPDATE_INTERVAL_HOURS, timeUnit = TimeUnit.HOURS, initialDelay = 0)
    public void updateQueueETA() {
        var records = dsl.selectFrom(QUEUEWAIT)
            .where(QUEUEWAIT.PRIO.isFalse()
                .and(QUEUEWAIT.START_QUEUE_TIME.ge(OffsetDateTime.now().minusDays(DATA_DAY_RANGE)))
                .and(QUEUEWAIT.QUEUE_TIME.ge(MIN_QUEUE_TIME_SECONDS)))
            .fetch()
            .into(QueuewaitRecord.class);
        if (records.isEmpty()) {
            LOGGER.error("No QueueWait records found?");
            return;
        }
        var powValues = records.stream()
            .mapToDouble(record -> Math.log(record.getQueueTime() / factor) / Math.log(record.getInitialQueueLen()))
            .toArray();
        pow = trimmedMean(powValues, 0.2);
        LOGGER.info("Updated queue ETA power to {}", pow);
    }

    private double trimmedMean(double[] values, double f) {
        Arrays.sort(values);
        var trimCount = (int) (values.length * f);
        var sum = 0.0;
        for (int i = trimCount; i < values.length - trimCount; i++) {
            sum += values[i];
        }
        var divisor = values.length - (2 * trimCount);
        if (divisor <= 0) throw new RuntimeException("Divisor must be > 0");
        return sum / divisor;
    }

    public double getPow() {
        return pow;
    }

    public double getFactor() {
        return factor;
    }
}
