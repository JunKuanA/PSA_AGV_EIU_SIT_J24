package module.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "fault_code_desc_list")
@XmlRootElement
public class fault_code_desc_list implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected Fault_code_desc_list_pk fault_code_desc_list_pk;
    @Column(name = "subcompo_m")
    private String subcompo_m;
    
    @Column(name = "active_i")
    private String active_i;
    
    public fault_code_desc_list() {
    }

    public Fault_code_desc_list_pk getFault_code_desc_list_pk() {
        return fault_code_desc_list_pk;
    }

    public void setFault_code_desc_list_pk(Fault_code_desc_list_pk fault_code_desc_list_pk) {
        this.fault_code_desc_list_pk = fault_code_desc_list_pk;
    }

    public String getSubcompo_m() {
        return subcompo_m;
    }

    public void setSubcompo_m(String subcompo_m) {
        this.subcompo_m = subcompo_m;
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
        result = prime * result + ((fault_code_desc_list_pk == null) ? 0 : fault_code_desc_list_pk.hashCode());
        result = prime * result + ((subcompo_m == null) ? 0 : subcompo_m.hashCode());
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
        fault_code_desc_list other = (fault_code_desc_list) obj;
        if (fault_code_desc_list_pk == null) {
            if (other.fault_code_desc_list_pk != null)
                return false;
        } else if (!fault_code_desc_list_pk.equals(other.fault_code_desc_list_pk))
            return false;
        if (subcompo_m == null) {
            if (other.subcompo_m != null)
                return false;
        } else if (!subcompo_m.equals(other.subcompo_m))
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
        return "fault_code_desc_list [fault_code_desc_list_pk=" + fault_code_desc_list_pk + "]";
    }
    
}
