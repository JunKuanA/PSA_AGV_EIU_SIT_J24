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
@Table(name = "jc_enq_info_r")
@XmlRootElement
@NamedQuery(name = "jc_enq_info_r.findByEventDt", query = "SELECT a FROM jc_enq_info_r a WHERE a.jc_enq_info_r_pk.event_dt = :eventDt")
public class jc_enq_info_r implements Serializable{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected embeddedPK jc_enq_info_r_pk;

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
    @Column(name = "jc_status_c")
    private String jc_status_c;

    @Basic(optional = false)
    @Column(name = "alert_mode")
    private String alert_mode;

    @Basic(optional = false)
    @Column(name = "mi_i")
    private String mi_i;

    @Basic(optional = false)
    @Column(name = "control_mode")
    private String control_mode;

    @Basic(optional = false)
    @Column(name = "barriers_down_i")
    private String barriers_down_i;

    @Basic(optional = false)
    @Column(name = "traffic_light_i")
    private String traffic_light_i;

    @Basic(optional = false)
    @Column(name = "mnl_xing_req_i")
    private String mnl_xing_req_i;

    @Basic(optional = false)
    @Column(name = "num_of_pm_loop")
    private int num_of_pm_loop;

    @Column(name = "pm_loop_list")
    private String pm_loop_list;

    @Basic(optional = false)
    @Column(name = "num_of_uturn_loop")
    private int num_of_uturn_loop;

    @Column(name = "uturn_loop_list")
    private String uturn_loop_list;

    @Basic(optional = false)
    @Column(name = "num_of_faults")
    private int num_of_faults;

    @Column(name = "fault_code_desc_list")
    private String fault_code_desc_list;
    
    @Basic(optional = false)
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    public jc_enq_info_r() {
    }
    public embeddedPK getJc_enq_info_r_pk() {
        return jc_enq_info_r_pk;
    }
    public void setJc_enq_info_r_pk(embeddedPK jc_enq_info_r_pk) {
        this.jc_enq_info_r_pk = jc_enq_info_r_pk;
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
    public String getJc_status_c() {
        return jc_status_c;
    }
    public void setJc_status_c(String jc_status_c) {
        this.jc_status_c = jc_status_c;
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
    public String getControl_mode() {
        return control_mode;
    }
    public void setControl_mode(String control_mode) {
        this.control_mode = control_mode;
    }

    public String getBarriers_down_i() {
        return barriers_down_i;
    }
    public void setBarriers_down_i(String barriers_down_i) {
        this.barriers_down_i = barriers_down_i;
    }
    public String getTraffic_light_i() {
        return traffic_light_i;
    }
    public void setTraffic_light_i(String traffic_light_i) {
        this.traffic_light_i = traffic_light_i;
    }
    public String getMnl_xing_req_i() {
        return mnl_xing_req_i;
    }
    public void setMnl_xing_req_i(String mnl_xing_req_i) {
        this.mnl_xing_req_i = mnl_xing_req_i;
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
    public int getNum_of_uturn_loop() {
        return num_of_uturn_loop;
    }
    public void setNum_of_uturn_loop(int num_of_uturn_loop) {
        this.num_of_uturn_loop = num_of_uturn_loop;
    }
    public String getUturn_loop_list() {
        return uturn_loop_list;
    }
    public void setUturn_loop_list(String uturn_loop_list) {
        this.uturn_loop_list = uturn_loop_list;
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
        result = prime * result + ((jc_enq_info_r_pk == null) ? 0 : jc_enq_info_r_pk.hashCode());
        result = prime * result + ((source_m == null) ? 0 : source_m.hashCode());
        result = prime * result + ((event_id == null) ? 0 : event_id.hashCode());
        result = prime * result + ((error_c == null) ? 0 : error_c.hashCode());
        result = prime * result + ((error_txt == null) ? 0 : error_txt.hashCode());
        result = prime * result + jc_id;
        result = prime * result + ((jc_m == null) ? 0 : jc_m.hashCode());
        result = prime * result + ((jc_status_c == null) ? 0 : jc_status_c.hashCode());
        result = prime * result + ((alert_mode == null) ? 0 : alert_mode.hashCode());
        result = prime * result + ((mi_i == null) ? 0 : mi_i.hashCode());
        result = prime * result + ((control_mode == null) ? 0 : control_mode.hashCode());
        result = prime * result + ((barriers_down_i == null) ? 0 : barriers_down_i.hashCode());
        result = prime * result + ((traffic_light_i == null) ? 0 : traffic_light_i.hashCode());
        result = prime * result + ((mnl_xing_req_i == null) ? 0 : mnl_xing_req_i.hashCode());
        result = prime * result + num_of_pm_loop;
        result = prime * result + ((pm_loop_list == null) ? 0 : pm_loop_list.hashCode());
        result = prime * result + num_of_uturn_loop;
        result = prime * result + ((uturn_loop_list == null) ? 0 : uturn_loop_list.hashCode());
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
        jc_enq_info_r other = (jc_enq_info_r) obj;
        if (jc_enq_info_r_pk == null) {
            if (other.jc_enq_info_r_pk != null)
                return false;
        } else if (!jc_enq_info_r_pk.equals(other.jc_enq_info_r_pk))
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
        if (jc_status_c == null) {
            if (other.jc_status_c != null)
                return false;
        } else if (!jc_status_c.equals(other.jc_status_c))
            return false;
        if (alert_mode == null) {
            if (other.alert_mode != null)
                return false;
        } else if (!alert_mode.equals(other.alert_mode))
            return false;
        if (mi_i == null) {
            if (other.mi_i != null)
                return false;
        } else if (!mi_i.equals(other.mi_i))
            return false;
        if (control_mode == null) {
            if (other.control_mode != null)
                return false;
        } else if (!control_mode.equals(other.control_mode))
            return false;
        if (barriers_down_i == null) {
            if (other.barriers_down_i != null)
                return false;
        } else if (!barriers_down_i.equals(other.barriers_down_i))
            return false;
        if (traffic_light_i == null) {
            if (other.traffic_light_i != null)
                return false;
        } else if (!traffic_light_i.equals(other.traffic_light_i))
            return false;
        if (mnl_xing_req_i == null) {
            if (other.mnl_xing_req_i != null)
                return false;
        } else if (!mnl_xing_req_i.equals(other.mnl_xing_req_i))
            return false;
        if (num_of_pm_loop != other.num_of_pm_loop)
            return false;
        if (pm_loop_list == null) {
            if (other.pm_loop_list != null)
                return false;
        } else if (!pm_loop_list.equals(other.pm_loop_list))
            return false;
        if (num_of_uturn_loop != other.num_of_uturn_loop)
            return false;
        if (uturn_loop_list == null) {
            if (other.uturn_loop_list != null)
                return false;
        } else if (!uturn_loop_list.equals(other.uturn_loop_list))
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
        return "jc_enq_info_r [jc_enq_info_r_pk=" + jc_enq_info_r_pk + "]";
    }
    
    
    
}
