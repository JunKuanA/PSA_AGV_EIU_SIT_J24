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
@Table(name = "jc_upd_st_r")
@XmlRootElement
public class jc_upd_st_r implements Serializable{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected embeddedPK jc_upd_st_r_pk;

    @Basic(optional = false)
    @Column(name = "source_m")
    private String source_m;

    @Basic(optional = false)
    @Column(name = "event_id")
    private String event_id;

    @Column(name = "error_c")
    private String error_c;

    @Column(name = "error_txt")
    private String error_txt;

    @Basic(optional = false)
    @Column(name = "jc_id")
    private int jc_id;

    @Basic(optional = false)
    @Column(name = "jc_m")
    private String jc_m;

    @Basic(optional = false)
    @Column(name = "curr_jc_status_c")
    private String curr_jc_status_c;

    @Basic(optional = false)
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public jc_upd_st_r() {
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public embeddedPK getJc_upd_st_r_pk() {
        return jc_upd_st_r_pk;
    }

    public void setJc_upd_st_r_pk(embeddedPK jc_upd_st_r_pk) {
        this.jc_upd_st_r_pk = jc_upd_st_r_pk;
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

    public String getError_c() {
        return error_c;
    }

    public void setError_c(String error_c) {
        this.error_c = error_c;
    }

    public String getError_txt() {
        return error_txt;
    }

    public void setError_txt(String error_txt) {
        this.error_txt = error_txt;
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

    public String getCurr_jc_status_c() {
        return curr_jc_status_c;
    }

    public void setCurr_jc_status_c(String curr_jc_status_c) {
        this.curr_jc_status_c = curr_jc_status_c;
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
        result = prime * result + ((jc_upd_st_r_pk == null) ? 0 : jc_upd_st_r_pk.hashCode());
        result = prime * result + ((source_m == null) ? 0 : source_m.hashCode());
        result = prime * result + ((event_id == null) ? 0 : event_id.hashCode());
        result = prime * result + ((error_c == null) ? 0 : error_c.hashCode());
        result = prime * result + ((error_txt == null) ? 0 : error_txt.hashCode());
        result = prime * result + jc_id;
        result = prime * result + ((jc_m == null) ? 0 : jc_m.hashCode());
        result = prime * result + ((curr_jc_status_c == null) ? 0 : curr_jc_status_c.hashCode());
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
        jc_upd_st_r other = (jc_upd_st_r) obj;
        if (jc_upd_st_r_pk == null) {
            if (other.jc_upd_st_r_pk != null)
                return false;
        } else if (!jc_upd_st_r_pk.equals(other.jc_upd_st_r_pk))
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
        if (error_c == null) {
            if (other.error_c != null)
                return false;
        } else if (!error_c.equals(other.error_c))
            return false;
        if (error_txt == null) {
            if (other.error_txt != null)
                return false;
        } else if (!error_txt.equals(other.error_txt))
            return false;
        if (jc_id != other.jc_id)
            return false;
        if (jc_m == null) {
            if (other.jc_m != null)
                return false;
        } else if (!jc_m.equals(other.jc_m))
            return false;
        if (curr_jc_status_c == null) {
            if (other.curr_jc_status_c != null)
                return false;
        } else if (!curr_jc_status_c.equals(other.curr_jc_status_c))
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
        return "jc_upd_st_r [jc_upd_st_r_pk=" + jc_upd_st_r_pk + "]";
    }

    
}
