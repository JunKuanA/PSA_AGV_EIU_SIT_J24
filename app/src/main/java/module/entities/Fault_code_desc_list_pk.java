package module.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class Fault_code_desc_list_pk implements Serializable{
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Basic(optional = false)
    @Column(name = "fault_code")
    private int fault_code;

    public Fault_code_desc_list_pk() {
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getFault_code() {
        return fault_code;
    }

    public void setFault_code(int fault_code) {
        this.fault_code = fault_code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + fault_code;
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
        Fault_code_desc_list_pk other = (Fault_code_desc_list_pk) obj;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        if (fault_code != other.fault_code)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Fault_code_desc_list_pk [timestamp=" + timestamp + ", fault_code=" + fault_code + "]";
    }

    
    
}
