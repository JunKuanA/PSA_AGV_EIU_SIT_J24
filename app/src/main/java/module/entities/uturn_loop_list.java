package module.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "uturn_loop_list")
@XmlRootElement
public class uturn_loop_list implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Basic(optional = false)
    @Column(name = "uturn_loop_m")
    private String uturn_loop_m;

    @Column(name = "active_i")
    private String active_i;

    public uturn_loop_list() {
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUturn_loop_m() {
        return uturn_loop_m;
    }

    public void setUturn_loop_m(String uturn_loop_m) {
        this.uturn_loop_m = uturn_loop_m;
    }

    public String getActive_i() {
        return active_i;
    }

    public void setActive_i(String active_i) {
        this.active_i = active_i;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((uturn_loop_m == null) ? 0 : uturn_loop_m.hashCode());
        result = prime * result + ((active_i == null) ? 0 : active_i.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        uturn_loop_list other = (uturn_loop_list) obj;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        if (uturn_loop_m == null) {
            if (other.uturn_loop_m != null)
                return false;
        } else if (!uturn_loop_m.equals(other.uturn_loop_m))
            return false;
        if (active_i == null) {
            if (other.active_i != null)
                return false;
        } else if (!active_i.equals(other.active_i))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "uturn_loop_list [timestamp=" + timestamp + ", uturn_loop_m=" + uturn_loop_m + ", active_i=" + active_i
                + "]";
    }
    
    
    
}
