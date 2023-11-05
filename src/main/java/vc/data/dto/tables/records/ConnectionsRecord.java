/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.records;


import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.TableRecordImpl;
import vc.data.dto.enums.Connectiontype;
import vc.data.dto.tables.Connections;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class ConnectionsRecord extends TableRecordImpl<ConnectionsRecord> implements Record4<OffsetDateTime, Connectiontype, String, UUID> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.connections.time</code>.
     */
    public ConnectionsRecord setTime(OffsetDateTime value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.connections.time</code>.
     */
    public OffsetDateTime getTime() {
        return (OffsetDateTime) get(0);
    }

    /**
     * Setter for <code>public.connections.connection</code>.
     */
    public ConnectionsRecord setConnection(Connectiontype value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.connections.connection</code>.
     */
    public Connectiontype getConnection() {
        return (Connectiontype) get(1);
    }

    /**
     * Setter for <code>public.connections.player_name</code>.
     */
    public ConnectionsRecord setPlayerName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.connections.player_name</code>.
     */
    public String getPlayerName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.connections.player_uuid</code>.
     */
    public ConnectionsRecord setPlayerUuid(UUID value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.connections.player_uuid</code>.
     */
    public UUID getPlayerUuid() {
        return (UUID) get(3);
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<OffsetDateTime, Connectiontype, String, UUID> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<OffsetDateTime, Connectiontype, String, UUID> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<OffsetDateTime> field1() {
        return Connections.CONNECTIONS.TIME;
    }

    @Override
    public Field<Connectiontype> field2() {
        return Connections.CONNECTIONS.CONNECTION;
    }

    @Override
    public Field<String> field3() {
        return Connections.CONNECTIONS.PLAYER_NAME;
    }

    @Override
    public Field<UUID> field4() {
        return Connections.CONNECTIONS.PLAYER_UUID;
    }

    @Override
    public OffsetDateTime component1() {
        return getTime();
    }

    @Override
    public Connectiontype component2() {
        return getConnection();
    }

    @Override
    public String component3() {
        return getPlayerName();
    }

    @Override
    public UUID component4() {
        return getPlayerUuid();
    }

    @Override
    public OffsetDateTime value1() {
        return getTime();
    }

    @Override
    public Connectiontype value2() {
        return getConnection();
    }

    @Override
    public String value3() {
        return getPlayerName();
    }

    @Override
    public UUID value4() {
        return getPlayerUuid();
    }

    @Override
    public ConnectionsRecord value1(OffsetDateTime value) {
        setTime(value);
        return this;
    }

    @Override
    public ConnectionsRecord value2(Connectiontype value) {
        setConnection(value);
        return this;
    }

    @Override
    public ConnectionsRecord value3(String value) {
        setPlayerName(value);
        return this;
    }

    @Override
    public ConnectionsRecord value4(UUID value) {
        setPlayerUuid(value);
        return this;
    }

    @Override
    public ConnectionsRecord values(OffsetDateTime value1, Connectiontype value2, String value3, UUID value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ConnectionsRecord
     */
    public ConnectionsRecord() {
        super(Connections.CONNECTIONS);
    }

    /**
     * Create a detached, initialised ConnectionsRecord
     */
    public ConnectionsRecord(OffsetDateTime time, Connectiontype connection, String playerName, UUID playerUuid) {
        super(Connections.CONNECTIONS);

        setTime(time);
        setConnection(connection);
        setPlayerName(playerName);
        setPlayerUuid(playerUuid);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised ConnectionsRecord
     */
    public ConnectionsRecord(vc.data.dto.tables.pojos.Connections value) {
        super(Connections.CONNECTIONS);

        if (value != null) {
            setTime(value.getTime());
            setConnection(value.getConnection());
            setPlayerName(value.getPlayerName());
            setPlayerUuid(value.getPlayerUuid());
            resetChangedOnNotNull();
        }
    }
}
