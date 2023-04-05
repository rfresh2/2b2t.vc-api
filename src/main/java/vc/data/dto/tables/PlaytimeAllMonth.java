/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables;


import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import vc.data.dto.Public;
import vc.data.dto.tables.records.PlaytimeAllMonthRecord;

import java.util.UUID;
import java.util.function.Function;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PlaytimeAllMonth extends TableImpl<PlaytimeAllMonthRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.playtime_all_month</code>
     */
    public static final PlaytimeAllMonth PLAYTIME_ALL_MONTH = new PlaytimeAllMonth();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PlaytimeAllMonthRecord> getRecordType() {
        return PlaytimeAllMonthRecord.class;
    }

    /**
     * The column <code>public.playtime_all_month.p_uuid</code>.
     */
    public final TableField<PlaytimeAllMonthRecord, UUID> P_UUID = createField(DSL.name("p_uuid"), SQLDataType.UUID, this, "");

    /**
     * The column <code>public.playtime_all_month.playtime</code>.
     */
    public final TableField<PlaytimeAllMonthRecord, Integer> PLAYTIME = createField(DSL.name("playtime"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.playtime_all_month.p_name</code>.
     */
    public final TableField<PlaytimeAllMonthRecord, String> P_NAME = createField(DSL.name("p_name"), SQLDataType.CLOB, this, "");

    private PlaytimeAllMonth(Name alias, Table<PlaytimeAllMonthRecord> aliased) {
        this(alias, aliased, new Field[] {
        });
    }

    private PlaytimeAllMonth(Name alias, Table<PlaytimeAllMonthRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.function());
    }

    /**
     * Create an aliased <code>public.playtime_all_month</code> table reference
     */
    public PlaytimeAllMonth(String alias) {
        this(DSL.name(alias), PLAYTIME_ALL_MONTH);
    }

    /**
     * Create an aliased <code>public.playtime_all_month</code> table reference
     */
    public PlaytimeAllMonth(Name alias) {
        this(alias, PLAYTIME_ALL_MONTH);
    }

    /**
     * Create a <code>public.playtime_all_month</code> table reference
     */
    public PlaytimeAllMonth() {
        this(DSL.name("playtime_all_month"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public PlaytimeAllMonth as(String alias) {
        return new PlaytimeAllMonth(DSL.name(alias), this, parameters);
    }

    @Override
    public PlaytimeAllMonth as(Name alias) {
        return new PlaytimeAllMonth(alias, this, parameters);
    }

    @Override
    public PlaytimeAllMonth as(Table<?> alias) {
        return new PlaytimeAllMonth(alias.getQualifiedName(), this, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public PlaytimeAllMonth rename(String name) {
        return new PlaytimeAllMonth(DSL.name(name), null, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public PlaytimeAllMonth rename(Name name) {
        return new PlaytimeAllMonth(name, null, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public PlaytimeAllMonth rename(Table<?> name) {
        return new PlaytimeAllMonth(name.getQualifiedName(), null, parameters);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<UUID, Integer, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Call this table-valued function
     */
    public PlaytimeAllMonth call() {
        PlaytimeAllMonth result = new PlaytimeAllMonth(DSL.name("playtime_all_month"), null, new Field[] {});

        return aliased() ? result.as(getUnqualifiedName()) : result;
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super UUID, ? super Integer, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super UUID, ? super Integer, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
