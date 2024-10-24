/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.pojos;


import java.io.Serializable;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TopDeathsMonthView implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String victimPlayerName;
    private final UUID victimPlayerUuid;
    private final Long deathCount;

    public TopDeathsMonthView(TopDeathsMonthView value) {
        this.victimPlayerName = value.victimPlayerName;
        this.victimPlayerUuid = value.victimPlayerUuid;
        this.deathCount = value.deathCount;
    }

    public TopDeathsMonthView(
        String victimPlayerName,
        UUID victimPlayerUuid,
        Long deathCount
    ) {
        this.victimPlayerName = victimPlayerName;
        this.victimPlayerUuid = victimPlayerUuid;
        this.deathCount = deathCount;
    }

    /**
     * Getter for <code>public.top_deaths_month_view.victim_player_name</code>.
     */
    public String getVictimPlayerName() {
        return this.victimPlayerName;
    }

    /**
     * Getter for <code>public.top_deaths_month_view.victim_player_uuid</code>.
     */
    public UUID getVictimPlayerUuid() {
        return this.victimPlayerUuid;
    }

    /**
     * Getter for <code>public.top_deaths_month_view.death_count</code>.
     */
    public Long getDeathCount() {
        return this.deathCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TopDeathsMonthView other = (TopDeathsMonthView) obj;
        if (this.victimPlayerName == null) {
            if (other.victimPlayerName != null)
                return false;
        }
        else if (!this.victimPlayerName.equals(other.victimPlayerName))
            return false;
        if (this.victimPlayerUuid == null) {
            if (other.victimPlayerUuid != null)
                return false;
        }
        else if (!this.victimPlayerUuid.equals(other.victimPlayerUuid))
            return false;
        if (this.deathCount == null) {
            if (other.deathCount != null)
                return false;
        }
        else if (!this.deathCount.equals(other.deathCount))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.victimPlayerName == null) ? 0 : this.victimPlayerName.hashCode());
        result = prime * result + ((this.victimPlayerUuid == null) ? 0 : this.victimPlayerUuid.hashCode());
        result = prime * result + ((this.deathCount == null) ? 0 : this.deathCount.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TopDeathsMonthView (");

        sb.append(victimPlayerName);
        sb.append(", ").append(victimPlayerUuid);
        sb.append(", ").append(deathCount);

        sb.append(")");
        return sb.toString();
    }
}
