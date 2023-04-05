/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.records;


import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;
import vc.data.dto.tables.PlaytimeAll;

import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PlaytimeAllRecord extends TableRecordImpl<PlaytimeAllRecord> implements Record2<UUID, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.playtime_all.p_uuid</code>.
     */
    public PlaytimeAllRecord setPUuid(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.playtime_all.p_uuid</code>.
     */
    public UUID getPUuid() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>public.playtime_all.playtime</code>.
     */
    public PlaytimeAllRecord setPlaytime(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.playtime_all.playtime</code>.
     */
    public Integer getPlaytime() {
        return (Integer) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<UUID, Integer> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<UUID, Integer> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return PlaytimeAll.PLAYTIME_ALL.P_UUID;
    }

    @Override
    public Field<Integer> field2() {
        return PlaytimeAll.PLAYTIME_ALL.PLAYTIME;
    }

    @Override
    public UUID component1() {
        return getPUuid();
    }

    @Override
    public Integer component2() {
        return getPlaytime();
    }

    @Override
    public UUID value1() {
        return getPUuid();
    }

    @Override
    public Integer value2() {
        return getPlaytime();
    }

    @Override
    public PlaytimeAllRecord value1(UUID value) {
        setPUuid(value);
        return this;
    }

    @Override
    public PlaytimeAllRecord value2(Integer value) {
        setPlaytime(value);
        return this;
    }

    @Override
    public PlaytimeAllRecord values(UUID value1, Integer value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PlaytimeAllRecord
     */
    public PlaytimeAllRecord() {
        super(PlaytimeAll.PLAYTIME_ALL);
    }

    /**
     * Create a detached, initialised PlaytimeAllRecord
     */
    public PlaytimeAllRecord(UUID pUuid, Integer playtime) {
        super(PlaytimeAll.PLAYTIME_ALL);

        setPUuid(pUuid);
        setPlaytime(playtime);
    }

    /**
     * Create a detached, initialised PlaytimeAllRecord
     */
    public PlaytimeAllRecord(vc.data.dto.tables.pojos.PlaytimeAll value) {
        super(PlaytimeAll.PLAYTIME_ALL);

        if (value != null) {
            setPUuid(value.getPUuid());
            setPlaytime(value.getPlaytime());
        }
    }
}
