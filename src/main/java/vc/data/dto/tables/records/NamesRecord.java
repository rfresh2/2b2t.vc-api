/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.records;


import org.jooq.impl.TableRecordImpl;
import vc.data.dto.tables.Names;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class NamesRecord extends TableRecordImpl<NamesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.names.name</code>.
     */
    public NamesRecord setName(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.names.name</code>.
     */
    public String getName() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.names.uuid</code>.
     */
    public NamesRecord setUuid(UUID value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.names.uuid</code>.
     */
    public UUID getUuid() {
        return (UUID) get(1);
    }

    /**
     * Setter for <code>public.names.changedtoat</code>.
     */
    public NamesRecord setChangedtoat(OffsetDateTime value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.names.changedtoat</code>.
     */
    public OffsetDateTime getChangedtoat() {
        return (OffsetDateTime) get(2);
    }

    /**
     * Setter for <code>public.names.changedfromat</code>.
     */
    public NamesRecord setChangedfromat(OffsetDateTime value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.names.changedfromat</code>.
     */
    public OffsetDateTime getChangedfromat() {
        return (OffsetDateTime) get(3);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached NamesRecord
     */
    public NamesRecord() {
        super(Names.NAMES);
    }

    /**
     * Create a detached, initialised NamesRecord
     */
    public NamesRecord(String name, UUID uuid, OffsetDateTime changedtoat, OffsetDateTime changedfromat) {
        super(Names.NAMES);

        setName(name);
        setUuid(uuid);
        setChangedtoat(changedtoat);
        setChangedfromat(changedfromat);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised NamesRecord
     */
    public NamesRecord(vc.data.dto.tables.pojos.Names value) {
        super(Names.NAMES);

        if (value != null) {
            setName(value.getName());
            setUuid(value.getUuid());
            setChangedtoat(value.getChangedtoat());
            setChangedfromat(value.getChangedfromat());
            resetChangedOnNotNull();
        }
    }
}
