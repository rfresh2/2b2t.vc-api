/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables;


import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import vc.data.dto.Public;
import vc.data.dto.tables.records.QueuewaitRecord;

import java.time.OffsetDateTime;
import java.util.Collection;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Queuewait extends TableImpl<QueuewaitRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.queuewait</code>
     */
    public static final Queuewait QUEUEWAIT = new Queuewait();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<QueuewaitRecord> getRecordType() {
        return QueuewaitRecord.class;
    }

    /**
     * The column <code>public.queuewait.id</code>.
     */
    public final TableField<QueuewaitRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.queuewait.player_name</code>.
     */
    public final TableField<QueuewaitRecord, String> PLAYER_NAME = createField(DSL.name("player_name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.queuewait.prio</code>.
     */
    public final TableField<QueuewaitRecord, Boolean> PRIO = createField(DSL.name("prio"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>public.queuewait.start_queue_time</code>.
     */
    public final TableField<QueuewaitRecord, OffsetDateTime> START_QUEUE_TIME = createField(DSL.name("start_queue_time"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "");

    /**
     * The column <code>public.queuewait.end_queue_time</code>.
     */
    public final TableField<QueuewaitRecord, OffsetDateTime> END_QUEUE_TIME = createField(DSL.name("end_queue_time"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "");

    /**
     * The column <code>public.queuewait.queue_time</code>.
     */
    public final TableField<QueuewaitRecord, Long> QUEUE_TIME = createField(DSL.name("queue_time"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.queuewait.initial_queue_len</code>.
     */
    public final TableField<QueuewaitRecord, Integer> INITIAL_QUEUE_LEN = createField(DSL.name("initial_queue_len"), SQLDataType.INTEGER.nullable(false), this, "");

    private Queuewait(Name alias, Table<QueuewaitRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Queuewait(Name alias, Table<QueuewaitRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.queuewait</code> table reference
     */
    public Queuewait(String alias) {
        this(DSL.name(alias), QUEUEWAIT);
    }

    /**
     * Create an aliased <code>public.queuewait</code> table reference
     */
    public Queuewait(Name alias) {
        this(alias, QUEUEWAIT);
    }

    /**
     * Create a <code>public.queuewait</code> table reference
     */
    public Queuewait() {
        this(DSL.name("queuewait"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<QueuewaitRecord, Integer> getIdentity() {
        return (Identity<QueuewaitRecord, Integer>) super.getIdentity();
    }

    @Override
    public Queuewait as(String alias) {
        return new Queuewait(DSL.name(alias), this);
    }

    @Override
    public Queuewait as(Name alias) {
        return new Queuewait(alias, this);
    }

    @Override
    public Queuewait as(Table<?> alias) {
        return new Queuewait(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Queuewait rename(String name) {
        return new Queuewait(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Queuewait rename(Name name) {
        return new Queuewait(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Queuewait rename(Table<?> name) {
        return new Queuewait(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Queuewait where(Condition condition) {
        return new Queuewait(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Queuewait where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Queuewait where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Queuewait where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Queuewait where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Queuewait where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Queuewait where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Queuewait where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Queuewait whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Queuewait whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
