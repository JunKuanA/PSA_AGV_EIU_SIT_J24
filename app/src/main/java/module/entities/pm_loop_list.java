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
@Table(name = "pm_loop_list")
@XmlRootElement
public class pm_loop_list implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Basic(optional = false)
    @Column(name = "pm_loop_m")
    private String pm_loop_m;

    @Column(name = "active_i")
    private String active_i;
    
    public pm_loop_list() {
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

    public String getPm_loop_m() {
        return pm_loop_m;
    }

    public void setPm_loop_m(String pm_loop_m) {
        this.pm_loop_m = pm_loop_m;
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
        result = prime * result + ((pm_loop_m == null) ? 0 : pm_loop_m.hashCode());
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
        pm_loop_list other = (pm_loop_list) obj;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        if (pm_loop_m == null) {
            if (other.pm_loop_m != null)
                return false;
        } else if (!pm_loop_m.equals(other.pm_loop_m))
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
        return "pm_loop_m [timestamp=" + timestamp + "]";
    }
    
    
}
