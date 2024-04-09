/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables;


import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import vc.data.dto.Public;
import vc.data.dto.tables.records.TopKillsMonthViewRecord;

import java.util.Collection;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TopKillsMonthView extends TableImpl<TopKillsMonthViewRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.top_kills_month_view</code>
     */
    public static final TopKillsMonthView TOP_KILLS_MONTH_VIEW = new TopKillsMonthView();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TopKillsMonthViewRecord> getRecordType() {
        return TopKillsMonthViewRecord.class;
    }

    /**
     * The column <code>public.top_kills_month_view.killer_player_name</code>.
     */
    public final TableField<TopKillsMonthViewRecord, String> KILLER_PLAYER_NAME = createField(DSL.name("killer_player_name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.top_kills_month_view.killer_player_uuid</code>.
     */
    public final TableField<TopKillsMonthViewRecord, UUID> KILLER_PLAYER_UUID = createField(DSL.name("killer_player_uuid"), SQLDataType.UUID, this, "");

    /**
     * The column <code>public.top_kills_month_view.kill_count</code>.
     */
    public final TableField<TopKillsMonthViewRecord, Long> KILL_COUNT = createField(DSL.name("kill_count"), SQLDataType.BIGINT, this, "");

    private TopKillsMonthView(Name alias, Table<TopKillsMonthViewRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private TopKillsMonthView(Name alias, Table<TopKillsMonthViewRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("""
        create view "top_kills_month_view" as  SELECT killer_player_name,
           killer_player_uuid,
           count(*) AS kill_count
          FROM deaths
         WHERE ((killer_player_uuid IS NOT NULL) AND ("time" >= (CURRENT_DATE - '30 days'::interval)))
         GROUP BY killer_player_name, killer_player_uuid
         ORDER BY (count(*)) DESC
        LIMIT 1000;
        """), where);
    }

    /**
     * Create an aliased <code>public.top_kills_month_view</code> table
     * reference
     */
    public TopKillsMonthView(String alias) {
        this(DSL.name(alias), TOP_KILLS_MONTH_VIEW);
    }

    /**
     * Create an aliased <code>public.top_kills_month_view</code> table
     * reference
     */
    public TopKillsMonthView(Name alias) {
        this(alias, TOP_KILLS_MONTH_VIEW);
    }

    /**
     * Create a <code>public.top_kills_month_view</code> table reference
     */
    public TopKillsMonthView() {
        this(DSL.name("top_kills_month_view"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public TopKillsMonthView as(String alias) {
        return new TopKillsMonthView(DSL.name(alias), this);
    }

    @Override
    public TopKillsMonthView as(Name alias) {
        return new TopKillsMonthView(alias, this);
    }

    @Override
    public TopKillsMonthView as(Table<?> alias) {
        return new TopKillsMonthView(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TopKillsMonthView rename(String name) {
        return new TopKillsMonthView(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TopKillsMonthView rename(Name name) {
        return new TopKillsMonthView(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TopKillsMonthView rename(Table<?> name) {
        return new TopKillsMonthView(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TopKillsMonthView where(Condition condition) {
        return new TopKillsMonthView(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TopKillsMonthView where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TopKillsMonthView where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TopKillsMonthView where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TopKillsMonthView where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TopKillsMonthView where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TopKillsMonthView where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TopKillsMonthView where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TopKillsMonthView whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TopKillsMonthView whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
