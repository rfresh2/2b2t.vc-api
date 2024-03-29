/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.records;


import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.TableRecordImpl;
import vc.data.dto.tables.GetUuidData;

import java.time.OffsetDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class GetUuidDataRecord extends TableRecordImpl<GetUuidDataRecord> implements Record3<OffsetDateTime, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.get_uuid_data.time</code>.
     */
    public GetUuidDataRecord setTime(OffsetDateTime value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.get_uuid_data.time</code>.
     */
    public OffsetDateTime getTime() {
        return (OffsetDateTime) get(0);
    }

    /**
     * Setter for <code>public.get_uuid_data.data</code>.
     */
    public GetUuidDataRecord setData(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.get_uuid_data.data</code>.
     */
    public String getData() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.get_uuid_data.table_name</code>.
     */
    public GetUuidDataRecord setTableName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.get_uuid_data.table_name</code>.
     */
    public String getTableName() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<OffsetDateTime, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<OffsetDateTime, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<OffsetDateTime> field1() {
        return GetUuidData.GET_UUID_DATA.TIME;
    }

    @Override
    public Field<String> field2() {
        return GetUuidData.GET_UUID_DATA.DATA;
    }

    @Override
    public Field<String> field3() {
        return GetUuidData.GET_UUID_DATA.TABLE_NAME;
    }

    @Override
    public OffsetDateTime component1() {
        return getTime();
    }

    @Override
    public String component2() {
        return getData();
    }

    @Override
    public String component3() {
        return getTableName();
    }

    @Override
    public OffsetDateTime value1() {
        return getTime();
    }

    @Override
    public String value2() {
        return getData();
    }

    @Override
    public String value3() {
        return getTableName();
    }

    @Override
    public GetUuidDataRecord value1(OffsetDateTime value) {
        setTime(value);
        return this;
    }

    @Override
    public GetUuidDataRecord value2(String value) {
        setData(value);
        return this;
    }

    @Override
    public GetUuidDataRecord value3(String value) {
        setTableName(value);
        return this;
    }

    @Override
    public GetUuidDataRecord values(OffsetDateTime value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GetUuidDataRecord
     */
    public GetUuidDataRecord() {
        super(GetUuidData.GET_UUID_DATA);
    }

    /**
     * Create a detached, initialised GetUuidDataRecord
     */
    public GetUuidDataRecord(OffsetDateTime time, String data, String tableName) {
        super(GetUuidData.GET_UUID_DATA);

        setTime(time);
        setData(data);
        setTableName(tableName);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised GetUuidDataRecord
     */
    public GetUuidDataRecord(vc.data.dto.tables.pojos.GetUuidData value) {
        super(GetUuidData.GET_UUID_DATA);

        if (value != null) {
            setTime(value.getTime());
            setData(value.getData());
            setTableName(value.getTableName());
            resetChangedOnNotNull();
        }
    }
}
