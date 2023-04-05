/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.records;


import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.TableRecordImpl;
import vc.data.dto.tables.PlaytimeMonthView;

import java.math.BigDecimal;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PlaytimeMonthViewRecord extends TableRecordImpl<PlaytimeMonthViewRecord> implements Record3<UUID, BigDecimal, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.playtime_month_view.p_uuid</code>.
     */
    public PlaytimeMonthViewRecord setPUuid(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.playtime_month_view.p_uuid</code>.
     */
    public UUID getPUuid() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>public.playtime_month_view.pt_days</code>.
     */
    public PlaytimeMonthViewRecord setPtDays(BigDecimal value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.playtime_month_view.pt_days</code>.
     */
    public BigDecimal getPtDays() {
        return (BigDecimal) get(1);
    }

    /**
     * Setter for <code>public.playtime_month_view.p_name</code>.
     */
    public PlaytimeMonthViewRecord setPName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.playtime_month_view.p_name</code>.
     */
    public String getPName() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<UUID, BigDecimal, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<UUID, BigDecimal, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return PlaytimeMonthView.PLAYTIME_MONTH_VIEW.P_UUID;
    }

    @Override
    public Field<BigDecimal> field2() {
        return PlaytimeMonthView.PLAYTIME_MONTH_VIEW.PT_DAYS;
    }

    @Override
    public Field<String> field3() {
        return PlaytimeMonthView.PLAYTIME_MONTH_VIEW.P_NAME;
    }

    @Override
    public UUID component1() {
        return getPUuid();
    }

    @Override
    public BigDecimal component2() {
        return getPtDays();
    }

    @Override
    public String component3() {
        return getPName();
    }

    @Override
    public UUID value1() {
        return getPUuid();
    }

    @Override
    public BigDecimal value2() {
        return getPtDays();
    }

    @Override
    public String value3() {
        return getPName();
    }

    @Override
    public PlaytimeMonthViewRecord value1(UUID value) {
        setPUuid(value);
        return this;
    }

    @Override
    public PlaytimeMonthViewRecord value2(BigDecimal value) {
        setPtDays(value);
        return this;
    }

    @Override
    public PlaytimeMonthViewRecord value3(String value) {
        setPName(value);
        return this;
    }

    @Override
    public PlaytimeMonthViewRecord values(UUID value1, BigDecimal value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PlaytimeMonthViewRecord
     */
    public PlaytimeMonthViewRecord() {
        super(PlaytimeMonthView.PLAYTIME_MONTH_VIEW);
    }

    /**
     * Create a detached, initialised PlaytimeMonthViewRecord
     */
    public PlaytimeMonthViewRecord(UUID pUuid, BigDecimal ptDays, String pName) {
        super(PlaytimeMonthView.PLAYTIME_MONTH_VIEW);

        setPUuid(pUuid);
        setPtDays(ptDays);
        setPName(pName);
    }

    /**
     * Create a detached, initialised PlaytimeMonthViewRecord
     */
    public PlaytimeMonthViewRecord(vc.data.dto.tables.pojos.PlaytimeMonthView value) {
        super(PlaytimeMonthView.PLAYTIME_MONTH_VIEW);

        if (value != null) {
            setPUuid(value.getPUuid());
            setPtDays(value.getPtDays());
            setPName(value.getPName());
        }
    }
}
