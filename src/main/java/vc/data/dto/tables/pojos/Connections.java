/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.pojos;


import vc.data.dto.enums.Connectiontype;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Connections implements Serializable {

    private static final long serialVersionUID = 1L;

    private final OffsetDateTime time;
    private final Connectiontype connection;
    private final String playerName;
    private final UUID playerUuid;

    public Connections(Connections value) {
        this.time = value.time;
        this.connection = value.connection;
        this.playerName = value.playerName;
        this.playerUuid = value.playerUuid;
    }

    public Connections(
        OffsetDateTime time,
        Connectiontype connection,
        String playerName,
        UUID playerUuid
    ) {
        this.time = time;
        this.connection = connection;
        this.playerName = playerName;
        this.playerUuid = playerUuid;
    }

    /**
     * Getter for <code>public.connections.time</code>.
     */
    public OffsetDateTime getTime() {
        return this.time;
    }

    /**
     * Getter for <code>public.connections.connection</code>.
     */
    public Connectiontype getConnection() {
        return this.connection;
    }

    /**
     * Getter for <code>public.connections.player_name</code>.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Getter for <code>public.connections.player_uuid</code>.
     */
    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Connections other = (Connections) obj;
        if (this.time == null) {
            if (other.time != null)
                return false;
        }
        else if (!this.time.equals(other.time))
            return false;
        if (this.connection == null) {
            if (other.connection != null)
                return false;
        }
        else if (!this.connection.equals(other.connection))
            return false;
        if (this.playerName == null) {
            if (other.playerName != null)
                return false;
        }
        else if (!this.playerName.equals(other.playerName))
            return false;
        if (this.playerUuid == null) {
            if (other.playerUuid != null)
                return false;
        }
        else if (!this.playerUuid.equals(other.playerUuid))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.time == null) ? 0 : this.time.hashCode());
        result = prime * result + ((this.connection == null) ? 0 : this.connection.hashCode());
        result = prime * result + ((this.playerName == null) ? 0 : this.playerName.hashCode());
        result = prime * result + ((this.playerUuid == null) ? 0 : this.playerUuid.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Connections (");

        sb.append(time);
        sb.append(", ").append(connection);
        sb.append(", ").append(playerName);
        sb.append(", ").append(playerUuid);

        sb.append(")");
        return sb.toString();
    }
}
