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
@Table(name = "tj_chg_mode_r")
@XmlRootElement
@NamedQuery(name = "tj_chg_mode_r.findByEventDt", query = "SELECT a FROM tj_chg_mode_r a WHERE a.tj_chg_mode_r_pk.event_dt = :eventDt")
public class tj_chg_mode_r implements Serializable{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected embeddedPK tj_chg_mode_r_pk;

    @Basic(optional = false)
    @Column(name = "source_m")
    private String source_m;

    @Basic(optional = false)
    @Column(name = "event_id")
    private String event_id;

    @Basic(optional = false)
    @Column(name = "tj_id")
    private int tj_id;

    @Basic(optional = false)
    @Column(name = "tj_m")
    private String tj_m;

    @Column(name = "error_c")
    private String error_c;

    @Column(name = "error_txt")
    private String error_txt;

    @Basic(optional = false)
    @Column(name = "curr_tj_mode_c")
    private String curr_tj_mode_c;

    @Basic(optional = false)
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public tj_chg_mode_r() {
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public embeddedPK getTj_chg_mode_r_pk() {
        return tj_chg_mode_r_pk;
    }

    public void setTj_chg_mode_r_pk(embeddedPK tj_chg_mode_r_pk) {
        this.tj_chg_mode_r_pk = tj_chg_mode_r_pk;
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
    public int getTj_id() {
        return tj_id;
    }

    public void setTj_id(int tj_id) {
        this.tj_id = tj_id;
    }

    public String getTj_m() {
        return tj_m;
    }

    public void setTj_m(String tj_m) {
        this.tj_m = tj_m;
    }

    public String getNew_tj_mode_c() {
        return curr_tj_mode_c;
    }

    public void setNew_tj_mode_c(String curr_tj_mode_c) {
        this.curr_tj_mode_c = curr_tj_mode_c;
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
        result = prime * result + ((tj_chg_mode_r_pk == null) ? 0 : tj_chg_mode_r_pk.hashCode());
        result = prime * result + ((source_m == null) ? 0 : source_m.hashCode());
        result = prime * result + ((event_id == null) ? 0 : event_id.hashCode());
        result = prime * result + ((error_c == null) ? 0 : error_c.hashCode());
        result = prime * result + ((error_txt == null) ? 0 : error_txt.hashCode());
        result = prime * result + tj_id;
        result = prime * result + ((tj_m == null) ? 0 : tj_m.hashCode());
        result = prime * result + ((curr_tj_mode_c == null) ? 0 : curr_tj_mode_c.hashCode());
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
        tj_chg_mode_r other = (tj_chg_mode_r) obj;
        if (tj_chg_mode_r_pk == null) {
            if (other.tj_chg_mode_r_pk != null)
                return false;
        } else if (!tj_chg_mode_r_pk.equals(other.tj_chg_mode_r_pk))
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
        if (tj_id != other.tj_id)
            return false;
        if (tj_m == null) {
            if (other.tj_m != null)
                return false;
        } else if (!tj_m.equals(other.tj_m))
            return false;
        if (curr_tj_mode_c == null) {
            if (other.curr_tj_mode_c != null)
                return false;
        } else if (!curr_tj_mode_c.equals(other.curr_tj_mode_c))
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
        return "tj_chg_mode_r [tj_chg_mode_r_pk=" + tj_chg_mode_r_pk + "]";
    }

}
