/*
 * This file is generated by jOOQ.
 */
package vc.data.dto;


import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import vc.data.dto.tables.*;


/**
 * A class modelling indexes of tables in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index CHATS_PLAYER_UUID_IDX = Internal.createIndex(DSL.name("chats_player_uuid_idx"), Chats.CHATS, new OrderField[] { Chats.CHATS.PLAYER_UUID }, false);
    public static final Index CHATS_TIME_IDX = Internal.createIndex(DSL.name("chats_time_idx"), Chats.CHATS, new OrderField[] { Chats.CHATS.TIME.desc() }, false);
    public static final Index CONNECTIONS_PLAYER_UUID_IDX = Internal.createIndex(DSL.name("connections_player_uuid_idx"), Connections.CONNECTIONS, new OrderField[] { Connections.CONNECTIONS.PLAYER_UUID }, false);
    public static final Index CONNECTIONS_TIME_IDX = Internal.createIndex(DSL.name("connections_time_idx"), Connections.CONNECTIONS, new OrderField[] { Connections.CONNECTIONS.TIME.desc() }, false);
    public static final Index DEATHS_KILLER_UUID_IDX = Internal.createIndex(DSL.name("deaths_killer_uuid_idx"), Deaths.DEATHS, new OrderField[] { Deaths.DEATHS.KILLER_PLAYER_UUID }, false);
    public static final Index DEATHS_TIME_IDX = Internal.createIndex(DSL.name("deaths_time_idx"), Deaths.DEATHS, new OrderField[] { Deaths.DEATHS.TIME.desc() }, false);
    public static final Index DEATHS_VICTIM_UUID_IDX = Internal.createIndex(DSL.name("deaths_victim_uuid_idx"), Deaths.DEATHS, new OrderField[] { Deaths.DEATHS.VICTIM_PLAYER_UUID }, false);
    public static final Index MAX_CONS_MONTH_VIEW_P_UUID_IDX = Internal.createIndex(DSL.name("max_cons_month_view_p_uuid_idx"), MaxConsMonthView.MAX_CONS_MONTH_VIEW, new OrderField[] { MaxConsMonthView.MAX_CONS_MONTH_VIEW.P_UUID }, true);
    public static final Index PLAYERCOUNT_TIME_IDX = Internal.createIndex(DSL.name("playercount_time_idx"), Playercount.PLAYERCOUNT, new OrderField[] { Playercount.PLAYERCOUNT.TIME.desc() }, false);
    public static final Index QUEUELENGTH_TIME_IDX = Internal.createIndex(DSL.name("queuelength_time_idx"), Queuelength.QUEUELENGTH, new OrderField[] { Queuelength.QUEUELENGTH.TIME.desc() }, false);
    public static final Index RESTARTS_TIME_IDX = Internal.createIndex(DSL.name("restarts_time_idx"), Restarts.RESTARTS, new OrderField[] { Restarts.RESTARTS.TIME.desc() }, false);
}
