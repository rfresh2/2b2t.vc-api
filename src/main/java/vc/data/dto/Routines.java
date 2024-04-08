/*
 * This file is generated by jOOQ.
 */
package vc.data.dto;


import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;
import vc.data.dto.routines.Playtime;
import vc.data.dto.routines.PlaytimeMonth;
import vc.data.dto.tables.GetUuidData;
import vc.data.dto.tables.PlayerStats;
import vc.data.dto.tables.records.GetUuidDataRecord;
import vc.data.dto.tables.records.PlayerStatsRecord;

import java.util.UUID;


/**
 * Convenience access to all stored procedures and functions in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Routines {

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
}
