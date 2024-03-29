/*
 * This file is generated by jOOQ.
 */
package vc.data.dto;


import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.types.YearToSecond;
import vc.data.dto.tables.*;
import vc.data.dto.tables.records.*;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * Convenience access to all tables in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>public.chats</code>.
     */
    public static final Chats CHATS = Chats.CHATS;

    /**
     * The table <code>public.connections</code>.
     */
    public static final Connections CONNECTIONS = Connections.CONNECTIONS;

    /**
     * The table <code>public.deaths</code>.
     */
    public static final Deaths DEATHS = Deaths.DEATHS;

    /**
     * The table <code>public.get_uuid_data</code>.
     */
    public static final GetUuidData GET_UUID_DATA = GetUuidData.GET_UUID_DATA;

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
    public static final MaxConsMonthView MAX_CONS_MONTH_VIEW = MaxConsMonthView.MAX_CONS_MONTH_VIEW;

    /**
     * The table <code>public.names</code>.
     */
    public static final Names NAMES = Names.NAMES;

    /**
     * The table <code>public.online_players</code>.
     */
    public static final OnlinePlayers ONLINE_PLAYERS = OnlinePlayers.ONLINE_PLAYERS;

    /**
     * The table <code>public.player_stats</code>.
     */
    public static final PlayerStats PLAYER_STATS = PlayerStats.PLAYER_STATS;

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
    public static final Playercount PLAYERCOUNT = Playercount.PLAYERCOUNT;

    /**
     * The table <code>public.playtime_all</code>.
     */
    public static final PlaytimeAll PLAYTIME_ALL = PlaytimeAll.PLAYTIME_ALL;

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
    public static final PlaytimeAllMonth PLAYTIME_ALL_MONTH = PlaytimeAllMonth.PLAYTIME_ALL_MONTH;

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
    public static final PlaytimeAllTimeInterval PLAYTIME_ALL_TIME_INTERVAL = PlaytimeAllTimeInterval.PLAYTIME_ALL_TIME_INTERVAL;

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
    public static final PlaytimeMonthView PLAYTIME_MONTH_VIEW = PlaytimeMonthView.PLAYTIME_MONTH_VIEW;

    /**
     * The table <code>public.queuelength</code>.
     */
    public static final Queuelength QUEUELENGTH = Queuelength.QUEUELENGTH;

    /**
     * The table <code>public.queuewait</code>.
     */
    public static final Queuewait QUEUEWAIT = Queuewait.QUEUEWAIT;

    /**
     * The table <code>public.restarts</code>.
     */
    public static final Restarts RESTARTS = Restarts.RESTARTS;

    /**
     * The table <code>public.tablist</code>.
     */
    public static final Tablist TABLIST = Tablist.TABLIST;
}
