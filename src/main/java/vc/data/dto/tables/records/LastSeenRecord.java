/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.records;


import org.jooq.impl.TableRecordImpl;
import vc.data.dto.tables.LastSeen;

import java.time.OffsetDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class LastSeenRecord extends TableRecordImpl<LastSeenRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.last_seen.last_seen</code>.
     */
    public LastSeenRecord setLastSeen(OffsetDateTime value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.last_seen.last_seen</code>.
     */
    public OffsetDateTime getLastSeen() {
        return (OffsetDateTime) get(0);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LastSeenRecord
     */
    public LastSeenRecord() {
        super(LastSeen.LAST_SEEN);
    }

    /**
     * Create a detached, initialised LastSeenRecord
     */
    public LastSeenRecord(OffsetDateTime lastSeen) {
        super(LastSeen.LAST_SEEN);

        setLastSeen(lastSeen);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised LastSeenRecord
     */
    public LastSeenRecord(vc.data.dto.tables.pojos.LastSeen value) {
        super(LastSeen.LAST_SEEN);

        if (value != null) {
            setLastSeen(value.getLastSeen());
            resetChangedOnNotNull();
        }
    }
}
