package module.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "jc_pm_dtec_info")
@XmlRootElement
public class jc_pm_dtec_info implements Serializable{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected embeddedPK jc_pm_dtec_info_pk;

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
    @Column(name = "num_of_pm_loop")
    private int num_of_pm_loop;

    @Basic(optional = false)
    @Column(name = "pm_loop_list")
    private String pm_loop_list;

    @Basic(optional = false)
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public jc_pm_dtec_info() {
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public embeddedPK getJc_pm_dtec_info_pk() {
        return jc_pm_dtec_info_pk;
    }

    public void setJc_pm_dtec_info_pk(embeddedPK jc_pm_dtec_info_pk) {
        this.jc_pm_dtec_info_pk = jc_pm_dtec_info_pk;
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

    public int getNum_of_pm_loop() {
        return num_of_pm_loop;
    }

    public void setNum_of_pm_loop(int num_of_pm_loop) {
        this.num_of_pm_loop = num_of_pm_loop;
    }

    public String getPm_loop_list() {
        return pm_loop_list;
    }

    public void setPm_loop_list(String pm_loop_list) {
        this.pm_loop_list = pm_loop_list;
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
        result = prime * result + ((jc_pm_dtec_info_pk == null) ? 0 : jc_pm_dtec_info_pk.hashCode());
        result = prime * result + ((source_m == null) ? 0 : source_m.hashCode());
        result = prime * result + ((event_id == null) ? 0 : event_id.hashCode());
        
        result = prime * result + jc_id;
        result = prime * result + ((jc_m == null) ? 0 : jc_m.hashCode());
        result = prime * result + num_of_pm_loop;
        result = prime * result + ((pm_loop_list == null) ? 0 : pm_loop_list.hashCode());
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
        jc_pm_dtec_info other = (jc_pm_dtec_info) obj;
        if (jc_pm_dtec_info_pk == null) {
            if (other.jc_pm_dtec_info_pk != null)
                return false;
        } else if (!jc_pm_dtec_info_pk.equals(other.jc_pm_dtec_info_pk))
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
        if (num_of_pm_loop != other.num_of_pm_loop)
            return false;
        if (pm_loop_list == null) {
            if (other.pm_loop_list != null)
                return false;
        } else if (!pm_loop_list.equals(other.pm_loop_list))
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
        return "jc_pm_dtec_info [jc_pm_dtec_info_pk=" + jc_pm_dtec_info_pk + "]";
    }

    
}
