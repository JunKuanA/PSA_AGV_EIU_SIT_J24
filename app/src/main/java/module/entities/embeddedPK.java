package module.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class embeddedPK implements Serializable{
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "event_dt")
    private String event_dt;
    
    @Basic(optional = false)
    @Column(name = "trans_id")
    private String trans_id;

    public embeddedPK() {
    }
    public String getEvent_dt() {
        return event_dt;
    }
    public void setEvent_dt(String event_dt) {
        this.event_dt = event_dt;
    }
    public String getTrans_id() {
        return trans_id;
    }
    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((event_dt == null) ? 0 : event_dt.hashCode());
        result = prime * result + ((trans_id == null) ? 0 : trans_id.hashCode());
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
        embeddedPK other = (embeddedPK) obj;
        if (event_dt == null) {
            if (other.event_dt != null)
                return false;
        } else if (!event_dt.equals(other.event_dt))
            return false;
        if (trans_id == null) {
            if (other.trans_id != null)
                return false;
        } else if (!trans_id.equals(other.trans_id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "embeddedPK [event_dt=" + event_dt + ", trans_id=" + trans_id + "]";
    }
    
}
