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
public class PlaytimeAll implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID pUuid;
    private final Integer playtime;

    public PlaytimeAll(PlaytimeAll value) {
        this.pUuid = value.pUuid;
        this.playtime = value.playtime;
    }

    public PlaytimeAll(
        UUID pUuid,
        Integer playtime
    ) {
        this.pUuid = pUuid;
        this.playtime = playtime;
    }

    /**
     * Getter for <code>public.playtime_all.p_uuid</code>.
     */
    public UUID getPUuid() {
        return this.pUuid;
    }

    /**
     * Getter for <code>public.playtime_all.playtime</code>.
     */
    public Integer getPlaytime() {
        return this.playtime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PlaytimeAll other = (PlaytimeAll) obj;
        if (this.pUuid == null) {
            if (other.pUuid != null)
                return false;
        }
        else if (!this.pUuid.equals(other.pUuid))
            return false;
        if (this.playtime == null) {
            if (other.playtime != null)
                return false;
        }
        else if (!this.playtime.equals(other.playtime))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.pUuid == null) ? 0 : this.pUuid.hashCode());
        result = prime * result + ((this.playtime == null) ? 0 : this.playtime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PlaytimeAll (");

        sb.append(pUuid);
        sb.append(", ").append(playtime);

        sb.append(")");
        return sb.toString();
    }
}
