/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.pojos;


import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Tablist implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String playerName;
    private final UUID playerUuid;
    private final OffsetDateTime time;

    public Tablist(Tablist value) {
        this.playerName = value.playerName;
        this.playerUuid = value.playerUuid;
        this.time = value.time;
    }

    public Tablist(
        String playerName,
        UUID playerUuid,
        OffsetDateTime time
    ) {
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.time = time;
    }

    /**
     * Getter for <code>public.tablist.player_name</code>.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Getter for <code>public.tablist.player_uuid</code>.
     */
    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    /**
     * Getter for <code>public.tablist.time</code>.
     */
    public OffsetDateTime getTime() {
        return this.time;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Tablist other = (Tablist) obj;
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
        if (this.time == null) {
            if (other.time != null)
                return false;
        }
        else if (!this.time.equals(other.time))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.playerName == null) ? 0 : this.playerName.hashCode());
        result = prime * result + ((this.playerUuid == null) ? 0 : this.playerUuid.hashCode());
        result = prime * result + ((this.time == null) ? 0 : this.time.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Tablist (");

        sb.append(playerName);
        sb.append(", ").append(playerUuid);
        sb.append(", ").append(time);

        sb.append(")");
        return sb.toString();
    }
}
