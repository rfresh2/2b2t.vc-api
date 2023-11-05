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
public class OnlinePlayers implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String playerName;
    private final UUID playerUuid;
    private final OffsetDateTime joinTime;

    public OnlinePlayers(OnlinePlayers value) {
        this.playerName = value.playerName;
        this.playerUuid = value.playerUuid;
        this.joinTime = value.joinTime;
    }

    public OnlinePlayers(
        String playerName,
        UUID playerUuid,
        OffsetDateTime joinTime
    ) {
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.joinTime = joinTime;
    }

    /**
     * Getter for <code>public.online_players.player_name</code>.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Getter for <code>public.online_players.player_uuid</code>.
     */
    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    /**
     * Getter for <code>public.online_players.join_time</code>.
     */
    public OffsetDateTime getJoinTime() {
        return this.joinTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OnlinePlayers other = (OnlinePlayers) obj;
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
        if (this.joinTime == null) {
            if (other.joinTime != null)
                return false;
        }
        else if (!this.joinTime.equals(other.joinTime))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.playerName == null) ? 0 : this.playerName.hashCode());
        result = prime * result + ((this.playerUuid == null) ? 0 : this.playerUuid.hashCode());
        result = prime * result + ((this.joinTime == null) ? 0 : this.joinTime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OnlinePlayers (");

        sb.append(playerName);
        sb.append(", ").append(playerUuid);
        sb.append(", ").append(joinTime);

        sb.append(")");
        return sb.toString();
    }
}
