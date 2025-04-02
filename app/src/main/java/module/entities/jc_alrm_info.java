package module.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "jc_alrm_info")
@XmlRootElement
@NamedQuery(name = "jc_alrm_info.findByfault",  query = "SELECT a FROM jc_alrm_info a WHERE a.fault_code_desc_list = :fault_code_desc_list ORDER BY a.timestamp DESC")
public class jc_alrm_info implements Serializable{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected embeddedPK jc_alrm_info_pk;

    @Basic(optional = false)
    @Column(name = "source_m")
    private String source_m;

    @Basic(optional = false)
    @Column(name = "event_id")
    private String event_id;


    @Basic(optional = false)
    @Column(name = "jc_id")
    private int jc_id;

    @Basic(optional = false)
    @Column(name = "jc_m")
    private String jc_m;

    @Basic(optional = false)
    @Column(name = "mi_i")
    private String mi_i;

    @Basic(optional = false)
    @Column(name = "alert_mode")
    private String alert_mode;

    @Basic(optional = false)
    @Column(name = "num_of_faults")
    private int num_of_faults;

    @Basic(optional = false)
    @Column(name = "fault_code_desc_list")
    private String fault_code_desc_list;
    
    @Basic(optional = false)
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    
    public jc_alrm_info() {
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public embeddedPK getJc_alrm_info_pk() {
        return jc_alrm_info_pk;
    }
    public void setJc_alrm_info_pk(embeddedPK jc_alrm_info_pk) {
        this.jc_alrm_info_pk = jc_alrm_info_pk;
    }
    public String getSource_m() {
        return source_m;
    }
    public void setSource_m(String source_m) {
        this.source_m = source_m;
    }
    public String getEvent_id() {
        return event_id;
    }
    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
    
    public int getJc_id() {
        return jc_id;
    }
    public void setJc_id(int jc_id) {
        this.jc_id = jc_id;
    }
    public String getJc_m() {
        return jc_m;
    }
    public void setJc_m(String jc_m) {
        this.jc_m = jc_m;
    }

    public String getAlert_mode() {
        return alert_mode;
    }

    public void setAlert_mode(String alert_mode) {
        this.alert_mode = alert_mode;
    }

    public String getMi_i() {
        return mi_i;
    }
    public void setMi_i(String mi_i) {
        this.mi_i = mi_i;
    }
    public int getNum_of_faults() {
        return num_of_faults;
    }
    public void setNum_of_faults(int num_of_faults) {
        this.num_of_faults = num_of_faults;
    }
    public String getFault_code_desc_list() {
        return fault_code_desc_list;
    }
    public void setFault_code_desc_list(String fault_code_desc_list) {
        this.fault_code_desc_list = fault_code_desc_list;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jc_alrm_info_pk == null) ? 0 : jc_alrm_info_pk.hashCode());
        result = prime * result + ((source_m == null) ? 0 : source_m.hashCode());
        result = prime * result + ((event_id == null) ? 0 : event_id.hashCode());
        result = prime * result + jc_id;
        result = prime * result + ((jc_m == null) ? 0 : jc_m.hashCode());
        result = prime * result + ((mi_i == null) ? 0 : mi_i.hashCode());
        result = prime * result + num_of_faults;
        result = prime * result + ((fault_code_desc_list == null) ? 0 : fault_code_desc_list.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
        jc_alrm_info other = (jc_alrm_info) obj;
        if (jc_alrm_info_pk == null) {
            if (other.jc_alrm_info_pk != null)
                return false;
        } else if (!jc_alrm_info_pk.equals(other.jc_alrm_info_pk))
            return false;
        if (source_m == null) {
            if (other.source_m != null)
                return false;
        } else if (!source_m.equals(other.source_m))
            return false;
        if (event_id == null) {
            if (other.event_id != null)
                return false;
        } else if (!event_id.equals(other.event_id))
            return false;
        
        if (jc_id != other.jc_id)
            return false;
        if (jc_m == null) {
            if (other.jc_m != null)
                return false;
        } else if (!jc_m.equals(other.jc_m))
            return false;
        if (mi_i == null) {
            if (other.mi_i != null)
                return false;
        } else if (!mi_i.equals(other.mi_i))
            return false;
        if (num_of_faults != other.num_of_faults)
            return false;
        if (fault_code_desc_list == null) {
            if (other.fault_code_desc_list != null)
                return false;
        } else if (!fault_code_desc_list.equals(other.fault_code_desc_list))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "jc_alrm_info [jc_alrm_info_pk=" + jc_alrm_info_pk + "]";
    }
}
