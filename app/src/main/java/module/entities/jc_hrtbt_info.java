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
@Table(name = "jc_hrtbt_info")
@XmlRootElement
public class jc_hrtbt_info implements Serializable{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected embeddedPK jc_hrtbt_info_pk;

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
    @Column(name = "control_mode")
    private String control_mode;

    @Basic(optional = false)
    @Column(name = "num_of_dead")
    private int num_of_dead;

    @Basic(optional = false)
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public jc_hrtbt_info() {
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public embeddedPK getJc_hrtbt_info_pk() {
        return jc_hrtbt_info_pk;
    }

    public void setJc_hrtbt_info_pk(embeddedPK jc_hrtbt_info_pk) {
        this.jc_hrtbt_info_pk = jc_hrtbt_info_pk;
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

    public String getControl_mode() {
        return control_mode;
    }

    public void setControl_mode(String control_mode) {
        this.control_mode = control_mode;
    }

    public int getNum_of_dead() {
        return num_of_dead;
    }

    public void setNum_of_dead(int num_of_dead) {
        this.num_of_dead = num_of_dead;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "jc_hrtbt_info [jc_hrtbt_info_pk=" + jc_hrtbt_info_pk + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jc_hrtbt_info_pk == null) ? 0 : jc_hrtbt_info_pk.hashCode());
        result = prime * result + ((source_m == null) ? 0 : source_m.hashCode());
        result = prime * result + ((event_id == null) ? 0 : event_id.hashCode());
        result = prime * result + jc_id;
        result = prime * result + ((jc_m == null) ? 0 : jc_m.hashCode());
        result = prime * result + ((control_mode == null) ? 0 : control_mode.hashCode());
        result = prime * result + num_of_dead;
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
        jc_hrtbt_info other = (jc_hrtbt_info) obj;
        if (jc_hrtbt_info_pk == null) {
            if (other.jc_hrtbt_info_pk != null)
                return false;
        } else if (!jc_hrtbt_info_pk.equals(other.jc_hrtbt_info_pk))
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
        if (control_mode == null) {
            if (other.control_mode != null)
                return false;
        } else if (!control_mode.equals(other.control_mode))
            return false;
        if (num_of_dead != other.num_of_dead)
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }
    
    
}
