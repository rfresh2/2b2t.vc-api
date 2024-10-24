/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.records;


import org.jooq.impl.TableRecordImpl;
import vc.data.dto.tables.Chats;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class ChatsRecord extends TableRecordImpl<ChatsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.chats.time</code>.
     */
    public ChatsRecord setTime(OffsetDateTime value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.chats.time</code>.
     */
    public OffsetDateTime getTime() {
        return (OffsetDateTime) get(0);
    }

    /**
     * Setter for <code>public.chats.chat</code>.
     */
    public ChatsRecord setChat(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.chats.chat</code>.
     */
    public String getChat() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.chats.player_name</code>.
     */
    public ChatsRecord setPlayerName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.chats.player_name</code>.
     */
    public String getPlayerName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.chats.player_uuid</code>.
     */
    public ChatsRecord setPlayerUuid(UUID value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.chats.player_uuid</code>.
     */
    public UUID getPlayerUuid() {
        return (UUID) get(3);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ChatsRecord
     */
    public ChatsRecord() {
        super(Chats.CHATS);
    }

    /**
     * Create a detached, initialised ChatsRecord
     */
    public ChatsRecord(OffsetDateTime time, String chat, String playerName, UUID playerUuid) {
        super(Chats.CHATS);

        setTime(time);
        setChat(chat);
        setPlayerName(playerName);
        setPlayerUuid(playerUuid);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised ChatsRecord
     */
    public ChatsRecord(vc.data.dto.tables.pojos.Chats value) {
        super(Chats.CHATS);

        if (value != null) {
            setTime(value.getTime());
            setChat(value.getChat());
            setPlayerName(value.getPlayerName());
            setPlayerUuid(value.getPlayerUuid());
            resetChangedOnNotNull();
        }
    }
}
