package vc.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import vc.controller.QueueController;
import vc.tablist.TablistRenderer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static vc.data.dto.Tables.TABLIST_TEXT;
import static vc.data.dto.tables.Queuelength.QUEUELENGTH;
import static vc.data.dto.tables.Tablist.TABLIST;

@org.springframework.stereotype.Component
public class TablistRenderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TablistRenderService.class);
    private static final int UPDATE_INTERVAL_MINUTES = 1;

    private final DSLContext dsl;
    private final TablistRenderer renderer;
    private volatile RenderedTablist rendered;
    private final QueueETAService queueETAService;

    public record RenderedTablist(byte[] png, Instant renderedAt) { }

    public TablistRenderService(
        final DSLContext dsl,
        final TablistRenderer renderer,
        final QueueETAService queueETAService
    ) {
        this.dsl = dsl;
        this.renderer = renderer;
        this.queueETAService = queueETAService;
    }

    @Scheduled(fixedDelay = UPDATE_INTERVAL_MINUTES, timeUnit = TimeUnit.MINUTES, initialDelay = 0)
    public void updateTablistRender() {
        try {
            List<TablistRenderer.TablistPlayer> players = dsl.selectFrom(TABLIST)
                .fetch()
                .stream()
                .map(t -> new TablistRenderer.TablistPlayer(t.getPlayerName(), t.getPlayerUuid()))
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .toList();
            var text = dsl.selectFrom(TABLIST_TEXT)
                .orderBy(TABLIST_TEXT.ID.desc())
                .limit(1)
                .fetchOne();
            Component header = text == null ? Component.empty() : parseText(text.getHeaderJson(), text.getHeaderText());
//            Component footer = text == null ? Component.empty() : parseText(text.getFooterJson(), text.getFooterText());
            var queueData = queueData();
            var queueEta = getEtaStringFromSeconds(queueETAService.getFactor() * (Math.pow(queueData.regular(), queueETAService.getPow())));
            Component footer = MiniMessage.miniMessage().deserialize(
                """
                <dark_gray><player_count> players online - Queue: <queue_len> - ETA: <queue_wait></dark_gray>
                
                <dark_gray>2b2t.vc</dark_gray>
                """,
                Placeholder.unparsed("queue_len", String.valueOf(queueData.regular())),
                Placeholder.unparsed("prio_len", String.valueOf(queueData.prio())),
                Placeholder.unparsed("queue_wait", queueEta),
                Placeholder.unparsed("player_count", String.valueOf(players.size()))
            );
            long start = System.nanoTime();
            byte[] png = renderer.render(players, header, footer);
            rendered = new RenderedTablist(png, Instant.now());
            LOGGER.info("Rendered tablist with {} players in {} ms", players.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
        } catch (final Exception e) {
            LOGGER.error("Failed rendering tablist", e);
        }
    }

    public String getEtaStringFromSeconds(final double totalSeconds) {
        final int hour = (int) (totalSeconds / 3600);
        final int minutes = (int) ((totalSeconds / 60) % 60);
        final int seconds = (int) (totalSeconds % 60);
        final String hourStr = hour >= 10 ? "" + hour : "0" + hour;
        final String minutesStr = minutes >= 10 ? "" + minutes : "0" + minutes;
        final String secondsStr = seconds >= 10 ? "" + seconds : "0" + seconds;
        return hourStr + ":" + minutesStr + ":" + secondsStr;
    }

    private QueueController.QueueData queueData() {
        return dsl
            .selectFrom(QUEUELENGTH)
            .orderBy(QUEUELENGTH.TIME.desc().nullsLast())
            .limit(1)
            .fetchOne()
            .into(QueueController.QueueData.class);
    }

    private Component parseText(final String json, final String plainText) {
        if (json != null) {
            try {
                return GsonComponentSerializer.gson().deserialize(json);
            } catch (final Exception e) {
                LOGGER.warn("Failed parsing tablist text component: {}", json, e);
            }
        }
        return plainText == null ? Component.empty() : Component.text(plainText);
    }

    /**
     * @return the latest render, or null if the first render hasn't completed yet
     */
    public RenderedTablist getRendered() {
        return rendered;
    }
}
