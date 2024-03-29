/*
 * This file is generated by jOOQ.
 */
package vc.data.dto;


import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.types.YearToSecond;
import vc.data.dto.routines.Playtime;
import vc.data.dto.routines.PlaytimeMonth;
import vc.data.dto.routines.PlaytimeTimeInterval;
import vc.data.dto.routines.RowEstimator;
import vc.data.dto.tables.*;
import vc.data.dto.tables.records.*;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * Convenience access to all stored procedures and functions in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Routines {


    /**
     * Call <code>public.playtime_all</code>.
     */
    public static Result<PlaytimeAllRecord> playtimeAll(
          Configuration configuration
    ) {
        return configuration.dsl().selectFrom(vc.data.dto.tables.PlaytimeAll.PLAYTIME_ALL.call(
        )).fetch();
    }

    /**
     * Call <code>public.playtime_month</code>
     */
    public static Integer playtimeMonth(
        Configuration configuration
        , UUID pUuid
    ) {
        PlaytimeMonth f = new PlaytimeMonth();
        f.setPUuid(pUuid);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.playtime_month</code> as a field.
     */
    public static Field<Integer> playtimeMonth(
        UUID pUuid
    ) {
        PlaytimeMonth f = new PlaytimeMonth();
        f.setPUuid(pUuid);

        return f.asField();
    }

    /**
     * Get <code>public.playtime_month</code> as a field.
     */
    public static Field<Integer> playtimeMonth(
        Field<UUID> pUuid
    ) {
        PlaytimeMonth f = new PlaytimeMonth();
        f.setPUuid(pUuid);

        return f.asField();
    }

    /**
     * Call <code>public.playtime_time_interval</code>
     */
    public static Integer playtimeTimeInterval(
        Configuration configuration
        , UUID pUuid
        , OffsetDateTime endTime
        , YearToSecond backInterval
    ) {
        PlaytimeTimeInterval f = new PlaytimeTimeInterval();
        f.setPUuid(pUuid);
        f.setEndTime(endTime);
        f.setBackInterval(backInterval);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.playtime_time_interval</code> as a field.
     */
    public static Field<Integer> playtimeTimeInterval(
        UUID pUuid
        , OffsetDateTime endTime
        , YearToSecond backInterval
    ) {
        PlaytimeTimeInterval f = new PlaytimeTimeInterval();
        f.setPUuid(pUuid);
        f.setEndTime(endTime);
        f.setBackInterval(backInterval);

        return f.asField();
    }

    /**
     * Get <code>public.playtime_time_interval</code> as a field.
     */
    public static Field<Integer> playtimeTimeInterval(
        Field<UUID> pUuid
        , Field<OffsetDateTime> endTime
        , Field<YearToSecond> backInterval
    ) {
        PlaytimeTimeInterval f = new PlaytimeTimeInterval();
        f.setPUuid(pUuid);
        f.setEndTime(endTime);
        f.setBackInterval(backInterval);

        return f.asField();
    }

    /**
     * Call <code>public.playtime</code>
     */
    public static Integer playtime(
        Configuration configuration
        , UUID pUuid
    ) {
        Playtime f = new Playtime();
        f.setPUuid(pUuid);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.playtime</code> as a field.
     */
    public static Field<Integer> playtime(
        UUID pUuid
    ) {
        Playtime f = new Playtime();
        f.setPUuid(pUuid);

        return f.asField();
    }

    /**
     * Get <code>public.playtime</code> as a field.
     */
    public static Field<Integer> playtime(
        Field<UUID> pUuid
    ) {
        Playtime f = new Playtime();
        f.setPUuid(pUuid);

        return f.asField();
    }

    /**
     * Call <code>public.get_uuid_data</code>.
     */
    public static Result<GetUuidDataRecord> getUuidData(
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
    public static GetUuidData getUuidData(
        UUID pUuid
    ) {
        return vc.data.dto.tables.GetUuidData.GET_UUID_DATA.call(
            pUuid
        );
    }

    /**
     * Get <code>public.get_uuid_data</code> as a table.
     */
    public static GetUuidData getUuidData(
        Field<UUID> pUuid
    ) {
        return vc.data.dto.tables.GetUuidData.GET_UUID_DATA.call(
            pUuid
        );
    }


    /**
     * Call <code>public.player_stats</code>.
     */
    public static Result<PlayerStatsRecord> playerStats(
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
    public static PlayerStats playerStats(
        UUID pUuid
    ) {
        return vc.data.dto.tables.PlayerStats.PLAYER_STATS.call(
            pUuid
        );
    }

    /**
     * Get <code>public.player_stats</code> as a table.
     */
    public static PlayerStats playerStats(
        Field<UUID> pUuid
    ) {
        return vc.data.dto.tables.PlayerStats.PLAYER_STATS.call(
            pUuid
        );
    }

    /**
     * Get <code>public.playtime_all</code> as a table.
     */
    public static PlaytimeAll playtimeAll() {
        return vc.data.dto.tables.PlaytimeAll.PLAYTIME_ALL.call(
        );
    }

    /**
     * Call <code>public.playtime_all_month</code>.
     */
    public static Result<PlaytimeAllMonthRecord> playtimeAllMonth(
          Configuration configuration
    ) {
        return configuration.dsl().selectFrom(vc.data.dto.tables.PlaytimeAllMonth.PLAYTIME_ALL_MONTH.call(
        )).fetch();
    }

    /**
     * Get <code>public.playtime_all_month</code> as a table.
     */
    public static PlaytimeAllMonth playtimeAllMonth() {
        return vc.data.dto.tables.PlaytimeAllMonth.PLAYTIME_ALL_MONTH.call(
        );
    }

    /**
     * Call <code>public.playtime_all_time_interval</code>.
     */
    public static Result<PlaytimeAllTimeIntervalRecord> playtimeAllTimeInterval(
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
    public static PlaytimeAllTimeInterval playtimeAllTimeInterval(
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
    public static PlaytimeAllTimeInterval playtimeAllTimeInterval(
          Field<OffsetDateTime> endT
        , Field<YearToSecond> tInterval
    ) {
        return vc.data.dto.tables.PlaytimeAllTimeInterval.PLAYTIME_ALL_TIME_INTERVAL.call(
            endT,
            tInterval
        );
    }

    /**
     * Call <code>public.row_estimator</code>
     */
    public static Long rowEstimator(
        Configuration configuration
        , String query
    ) {
        RowEstimator f = new RowEstimator();
        f.setQuery(query);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.row_estimator</code> as a field.
     */
    public static Field<Long> rowEstimator(
        String query
    ) {
        RowEstimator f = new RowEstimator();
        f.setQuery(query);

        return f.asField();
    }

    /**
     * Get <code>public.row_estimator</code> as a field.
     */
    public static Field<Long> rowEstimator(
        Field<String> query
    ) {
        RowEstimator f = new RowEstimator();
        f.setQuery(query);

        return f.asField();
    }
}
