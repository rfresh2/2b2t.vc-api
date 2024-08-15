/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables;


import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import vc.data.dto.Public;
import vc.data.dto.tables.records.LastSeenRecord;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class LastSeen extends TableImpl<LastSeenRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.last_seen</code>
     */
    public static final LastSeen LAST_SEEN = new LastSeen();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LastSeenRecord> getRecordType() {
        return LastSeenRecord.class;
    }

    /**
     * The column <code>public.last_seen.last_seen</code>.
     */
    public final TableField<LastSeenRecord, OffsetDateTime> LAST_SEEN_ = createField(DSL.name("last_seen"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "");

    private LastSeen(Name alias, Table<LastSeenRecord> aliased) {
        this(alias, aliased, new Field[] {
            DSL.val(null, SQLDataType.UUID)
        });
    }

    private LastSeen(Name alias, Table<LastSeenRecord> aliased, Field<?>[] parameters) {
        this(alias, aliased, parameters, null);
    }

    private LastSeen(Name alias, Table<LastSeenRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.function(), where);
    }

    /**
     * Create an aliased <code>public.last_seen</code> table reference
     */
    public LastSeen(String alias) {
        this(DSL.name(alias), LAST_SEEN);
    }

    /**
     * Create an aliased <code>public.last_seen</code> table reference
     */
    public LastSeen(Name alias) {
        this(alias, LAST_SEEN);
    }

    /**
     * Create a <code>public.last_seen</code> table reference
     */
    public LastSeen() {
        this(DSL.name("last_seen"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public LastSeen as(String alias) {
        return new LastSeen(DSL.name(alias), this, parameters);
    }

    @Override
    public LastSeen as(Name alias) {
        return new LastSeen(alias, this, parameters);
    }

    @Override
    public LastSeen as(Table<?> alias) {
        return new LastSeen(alias.getQualifiedName(), this, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public LastSeen rename(String name) {
        return new LastSeen(DSL.name(name), null, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public LastSeen rename(Name name) {
        return new LastSeen(name, null, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public LastSeen rename(Table<?> name) {
        return new LastSeen(name.getQualifiedName(), null, parameters);
    }

    /**
     * Call this table-valued function
     */
    public LastSeen call(
          UUID pUuid
    ) {
        LastSeen result = new LastSeen(DSL.name("last_seen"), null, new Field[] {
            DSL.val(pUuid, SQLDataType.UUID)
        });

        return aliased() ? result.as(getUnqualifiedName()) : result;
    }

    /**
     * Call this table-valued function
     */
    public LastSeen call(
          Field<UUID> pUuid
    ) {
        LastSeen result = new LastSeen(DSL.name("last_seen"), null, new Field[] {
            pUuid
        });

        return aliased() ? result.as(getUnqualifiedName()) : result;
    }
}
