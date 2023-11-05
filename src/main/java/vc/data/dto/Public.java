/*
 * This file is generated by jOOQ.
 */
package vc.data.dto;


import org.jooq.*;
import org.jooq.impl.SchemaImpl;
import org.jooq.types.YearToSecond;
import vc.data.dto.tables.*;
import vc.data.dto.tables.records.*;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.chats</code>.
     */
    public final Chats CHATS = Chats.CHATS;

    /**
     * The table <code>public.connections</code>.
     */
    public final Connections CONNECTIONS = Connections.CONNECTIONS;

    /**
     * The table <code>public.deaths</code>.
     */
    public final Deaths DEATHS = Deaths.DEATHS;

    /**
     * The table <code>public.get_uuid_data</code>.
     */
    public final GetUuidData GET_UUID_DATA = GetUuidData.GET_UUID_DATA;

    /**
     * Call <code>public.get_uuid_data</code>.
     */
    public static Result<GetUuidDataRecord> GET_UUID_DATA(
        Configuration configuration
        , UUID pUuid
    ) {
        return configuration.dsl().selectFrom(vc.data.dto.tables.GetUuidData.GET_UUID_DATA.call(
            pUuid
        )).fetch();
    }

    /**
     * Get <code>public.get_uuid_data</code> as a table.
     */
    public static GetUuidData GET_UUID_DATA(
        UUID pUuid
    ) {
        return vc.data.dto.tables.GetUuidData.GET_UUID_DATA.call(
            pUuid
        );
    }

    /**
     * Get <code>public.get_uuid_data</code> as a table.
     */
    public static GetUuidData GET_UUID_DATA(
        Field<UUID> pUuid
    ) {
        return vc.data.dto.tables.GetUuidData.GET_UUID_DATA.call(
            pUuid
        );
    }

    /**
     * The table <code>public.max_cons_month_view</code>.
     */
    public final MaxConsMonthView MAX_CONS_MONTH_VIEW = MaxConsMonthView.MAX_CONS_MONTH_VIEW;

    /**
     * The table <code>public.names</code>.
     */
    public final Names NAMES = Names.NAMES;

    /**
     * The table <code>public.online_players</code>.
     */
    public final OnlinePlayers ONLINE_PLAYERS = OnlinePlayers.ONLINE_PLAYERS;

    /**
     * The table <code>public.player_stats</code>.
     */
    public final PlayerStats PLAYER_STATS = PlayerStats.PLAYER_STATS;

    /**
     * Call <code>public.player_stats</code>.
     */
    public static Result<PlayerStatsRecord> PLAYER_STATS(
        Configuration configuration
        , UUID pUuid
    ) {
        return configuration.dsl().selectFrom(vc.data.dto.tables.PlayerStats.PLAYER_STATS.call(
            pUuid
        )).fetch();
    }

    /**
     * Get <code>public.player_stats</code> as a table.
     */
    public static PlayerStats PLAYER_STATS(
        UUID pUuid
    ) {
        return vc.data.dto.tables.PlayerStats.PLAYER_STATS.call(
            pUuid
        );
    }

    /**
     * Get <code>public.player_stats</code> as a table.
     */
    public static PlayerStats PLAYER_STATS(
        Field<UUID> pUuid
    ) {
        return vc.data.dto.tables.PlayerStats.PLAYER_STATS.call(
            pUuid
        );
    }

    /**
     * The table <code>public.playercount</code>.
     */
    public final Playercount PLAYERCOUNT = Playercount.PLAYERCOUNT;

    /**
     * The table <code>public.playtime_all</code>.
     */
    public final PlaytimeAll PLAYTIME_ALL = PlaytimeAll.PLAYTIME_ALL;

    /**
     * Call <code>public.playtime_all</code>.
     */
    public static Result<PlaytimeAllRecord> PLAYTIME_ALL(
        Configuration configuration
    ) {
        return configuration.dsl().selectFrom(vc.data.dto.tables.PlaytimeAll.PLAYTIME_ALL.call(
        )).fetch();
    }

    /**
     * Get <code>public.playtime_all</code> as a table.
     */
    public static PlaytimeAll PLAYTIME_ALL() {
        return vc.data.dto.tables.PlaytimeAll.PLAYTIME_ALL.call(
        );
    }

    /**
     * The table <code>public.playtime_all_month</code>.
     */
    public final PlaytimeAllMonth PLAYTIME_ALL_MONTH = PlaytimeAllMonth.PLAYTIME_ALL_MONTH;

    /**
     * Call <code>public.playtime_all_month</code>.
     */
    public static Result<PlaytimeAllMonthRecord> PLAYTIME_ALL_MONTH(
        Configuration configuration
    ) {
        return configuration.dsl().selectFrom(vc.data.dto.tables.PlaytimeAllMonth.PLAYTIME_ALL_MONTH.call(
        )).fetch();
    }

    /**
     * Get <code>public.playtime_all_month</code> as a table.
     */
    public static PlaytimeAllMonth PLAYTIME_ALL_MONTH() {
        return vc.data.dto.tables.PlaytimeAllMonth.PLAYTIME_ALL_MONTH.call(
        );
    }

    /**
     * The table <code>public.playtime_all_time_interval</code>.
     */
    public final PlaytimeAllTimeInterval PLAYTIME_ALL_TIME_INTERVAL = PlaytimeAllTimeInterval.PLAYTIME_ALL_TIME_INTERVAL;

    /**
     * Call <code>public.playtime_all_time_interval</code>.
     */
    public static Result<PlaytimeAllTimeIntervalRecord> PLAYTIME_ALL_TIME_INTERVAL(
        Configuration configuration
        , OffsetDateTime endT
        , YearToSecond tInterval
    ) {
        return configuration.dsl().selectFrom(vc.data.dto.tables.PlaytimeAllTimeInterval.PLAYTIME_ALL_TIME_INTERVAL.call(
            endT
            , tInterval
        )).fetch();
    }

    /**
     * Get <code>public.playtime_all_time_interval</code> as a table.
     */
    public static PlaytimeAllTimeInterval PLAYTIME_ALL_TIME_INTERVAL(
        OffsetDateTime endT
        , YearToSecond tInterval
    ) {
        return vc.data.dto.tables.PlaytimeAllTimeInterval.PLAYTIME_ALL_TIME_INTERVAL.call(
            endT,
            tInterval
        );
    }

    /**
     * Get <code>public.playtime_all_time_interval</code> as a table.
     */
    public static PlaytimeAllTimeInterval PLAYTIME_ALL_TIME_INTERVAL(
        Field<OffsetDateTime> endT
        , Field<YearToSecond> tInterval
    ) {
        return vc.data.dto.tables.PlaytimeAllTimeInterval.PLAYTIME_ALL_TIME_INTERVAL.call(
            endT,
            tInterval
        );
    }

    /**
     * The table <code>public.playtime_month_view</code>.
     */
    public final PlaytimeMonthView PLAYTIME_MONTH_VIEW = PlaytimeMonthView.PLAYTIME_MONTH_VIEW;

    /**
     * The table <code>public.queuelength</code>.
     */
    public final Queuelength QUEUELENGTH = Queuelength.QUEUELENGTH;

    /**
     * The table <code>public.queuewait</code>.
     */
    public final Queuewait QUEUEWAIT = Queuewait.QUEUEWAIT;

    /**
     * The table <code>public.restarts</code>.
     */
    public final Restarts RESTARTS = Restarts.RESTARTS;

    /**
     * The table <code>public.tablist</code>.
     */
    public final Tablist TABLIST = Tablist.TABLIST;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Chats.CHATS,
            Connections.CONNECTIONS,
            Deaths.DEATHS,
            GetUuidData.GET_UUID_DATA,
            MaxConsMonthView.MAX_CONS_MONTH_VIEW,
            Names.NAMES,
            OnlinePlayers.ONLINE_PLAYERS,
            PlayerStats.PLAYER_STATS,
            Playercount.PLAYERCOUNT,
            PlaytimeAll.PLAYTIME_ALL,
            PlaytimeAllMonth.PLAYTIME_ALL_MONTH,
            PlaytimeAllTimeInterval.PLAYTIME_ALL_TIME_INTERVAL,
            PlaytimeMonthView.PLAYTIME_MONTH_VIEW,
            Queuelength.QUEUELENGTH,
            Queuewait.QUEUEWAIT,
            Restarts.RESTARTS,
            Tablist.TABLIST
        );
    }
}
