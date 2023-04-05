/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;
import vc.data.dto.tables.Restarts;

import java.time.OffsetDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RestartsRecord extends UpdatableRecordImpl<RestartsRecord> implements Record2<OffsetDateTime, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.restarts.time</code>.
     */
    public RestartsRecord setTime(OffsetDateTime value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.restarts.time</code>.
     */
    public OffsetDateTime getTime() {
        return (OffsetDateTime) get(0);
    }

    /**
     * Setter for <code>public.restarts.id</code>.
     */
    public RestartsRecord setId(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.restarts.id</code>.
     */
    public Integer getId() {
        return (Integer) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<OffsetDateTime, Integer> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<OffsetDateTime, Integer> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<OffsetDateTime> field1() {
        return Restarts.RESTARTS.TIME;
    }

    @Override
    public Field<Integer> field2() {
        return Restarts.RESTARTS.ID;
    }

    @Override
    public OffsetDateTime component1() {
        return getTime();
    }

    @Override
    public Integer component2() {
        return getId();
    }

    @Override
    public OffsetDateTime value1() {
        return getTime();
    }

    @Override
    public Integer value2() {
        return getId();
    }

    @Override
    public RestartsRecord value1(OffsetDateTime value) {
        setTime(value);
        return this;
    }

    @Override
    public RestartsRecord value2(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public RestartsRecord values(OffsetDateTime value1, Integer value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RestartsRecord
     */
    public RestartsRecord() {
        super(Restarts.RESTARTS);
    }

    /**
     * Create a detached, initialised RestartsRecord
     */
    public RestartsRecord(OffsetDateTime time, Integer id) {
        super(Restarts.RESTARTS);

        setTime(time);
        setId(id);
    }

    /**
     * Create a detached, initialised RestartsRecord
     */
    public RestartsRecord(vc.data.dto.tables.pojos.Restarts value) {
        super(Restarts.RESTARTS);

        if (value != null) {
            setTime(value.getTime());
            setId(value.getId());
        }
    }
}
