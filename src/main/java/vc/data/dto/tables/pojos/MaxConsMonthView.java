/*
 * This file is generated by jOOQ.
 */
package vc.data.dto.tables.pojos;


import java.io.Serializable;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MaxConsMonthView implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID pUuid;
    private final Integer maxCons;
    private final String pName;

    public MaxConsMonthView(MaxConsMonthView value) {
        this.pUuid = value.pUuid;
        this.maxCons = value.maxCons;
        this.pName = value.pName;
    }

    public MaxConsMonthView(
        UUID pUuid,
        Integer maxCons,
        String pName
    ) {
        this.pUuid = pUuid;
        this.maxCons = maxCons;
        this.pName = pName;
    }

    /**
     * Getter for <code>public.max_cons_month_view.p_uuid</code>.
     */
    public UUID getPUuid() {
        return this.pUuid;
    }

    /**
     * Getter for <code>public.max_cons_month_view.max_cons</code>.
     */
    public Integer getMaxCons() {
        return this.maxCons;
    }

    /**
     * Getter for <code>public.max_cons_month_view.p_name</code>.
     */
    public String getPName() {
        return this.pName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MaxConsMonthView other = (MaxConsMonthView) obj;
        if (this.pUuid == null) {
            if (other.pUuid != null)
                return false;
        }
        else if (!this.pUuid.equals(other.pUuid))
            return false;
        if (this.maxCons == null) {
            if (other.maxCons != null)
                return false;
        }
        else if (!this.maxCons.equals(other.maxCons))
            return false;
        if (this.pName == null) {
            if (other.pName != null)
                return false;
        }
        else if (!this.pName.equals(other.pName))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.pUuid == null) ? 0 : this.pUuid.hashCode());
        result = prime * result + ((this.maxCons == null) ? 0 : this.maxCons.hashCode());
        result = prime * result + ((this.pName == null) ? 0 : this.pName.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MaxConsMonthView (");

        sb.append(pUuid);
        sb.append(", ").append(maxCons);
        sb.append(", ").append(pName);

        sb.append(")");
        return sb.toString();
    }
}
