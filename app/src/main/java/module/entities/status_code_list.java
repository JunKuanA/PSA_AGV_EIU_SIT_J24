package module.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "status_code_list")
@XmlRootElement
@NamedQuery(name = "status_code_list.findByStatusCode", query = "SELECT g FROM status_code_list g WHERE g.status_code = :statusCode")
public class status_code_list implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "status_code")
    private int status_code;

    @Column(name = "status_desc")
    private String status_desc;

    public String getOPCUA_tag_name() {
        return OPCUA_tag_name;
    }

    public void setOPCUA_tag_name(String OPCUA_tag_name) {
        this.OPCUA_tag_name = OPCUA_tag_name;
    }

    @Column(name = "OPCUA_tag_name")
    private String OPCUA_tag_name;

    public status_code_list() {
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getStatus_desc() {
        return status_desc;
    }

    public void setStatus_desc(String status_desc) {
        this.status_desc = status_desc;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((status_desc == null) ? 0 : status_desc.hashCode());
        result = prime * result + ((OPCUA_tag_name == null) ? 0 : OPCUA_tag_name.hashCode());
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
        status_code_list other = (status_code_list) obj;
        if (status_desc == null) {
            if (other.status_desc != null)
                return false;
        } else if (!status_desc.equals(other.status_desc))
            return false;
        if (OPCUA_tag_name == null) {
            if (other.OPCUA_tag_name != null)
                return false;
        } else if (!OPCUA_tag_name.equals(other.OPCUA_tag_name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "status_code_list [status_desc=" + status_desc + ", OPCUA_tag_name=" + OPCUA_tag_name + "]";
    }
}
