package module.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "equipment_info")
@XmlRootElement
@NamedQuery(name = "equipment_info.findAll", query = "SELECT eqpt FROM equipment_info eqpt")
@NamedQuery(name = "equipment_info.findByEqptId", query = "SELECT eqpt FROM equipment_info eqpt WHERE eqpt.eqpt_id = :eqpt_id")
@NamedQuery(name = "equipment_info.findByEqptName", query = "SELECT eqpt FROM equipment_info eqpt WHERE eqpt.eqpt_name = :eqpt_name")
@NamedQuery(name = "equipment_info.equipmentHB", query = "SELECT eqpt FROM equipment_info eqpt WHERE eqpt.eqpt_name = 'eiu1' OR eqpt.eqpt_name = 'eiu2' OR eqpt.eqpt_name = 'plc' OR eqpt.eqpt_name = 'apms' or eqpt.eqpt_name = 'JN2004'")
@NamedQuery(name = "equipment_info.eiu1HB", query = "UPDATE equipment_info eqpt SET eqpt.last_hb = ?1 WHERE eqpt.eqpt_name = 'eiu1'")
@NamedQuery(name = "equipment_info.PLCHB", query = "UPDATE equipment_info eqpt SET eqpt.last_hb = ?1 WHERE eqpt.eqpt_name = 'plc'")
@NamedQuery(name = "equipment_info.jcHB", query = "UPDATE equipment_info eqpt SET eqpt.last_hb = ?1 WHERE eqpt.eqpt_name = 'JN2004'")
@NamedQuery(name = "equipment_info.equipmentLastComm", query = "SELECT eqpt.last_comm FROM equipment_info eqpt WHERE eqpt.eqpt_name = 'plc'")
@NamedQuery(name = "equipment_info.jcLastCom", query = "SELECT eqpt.last_comm FROM equipment_info eqpt WHERE eqpt.eqpt_name = 'JN2004'")

public class equipment_info implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "eqpt_id")
    private String eqpt_id;

    @Basic(optional = false)
    @Column(name = "eqpt_name")
    private String eqpt_name;

    @Column(name = "eqpt_eiu")
    private String eqpt_eiu;

    @Column(name = "last_hb")
    private Integer last_hb;
    
    @Basic(optional = false)
    @Column(name = "last_comm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date last_comm;

    public equipment_info() {
    }

    public String getEqpt_id() {
        return eqpt_id;
    }
    public void setEqpt_id(String eqpt_id) {
        this.eqpt_id = eqpt_id;
    }
    public String getEqpt_name() {
        return eqpt_name;
    }
    public void setEqpt_name(String eqpt_name) {
        this.eqpt_name = eqpt_name;
    }
    public String getEqpt_eiu() {
        return eqpt_eiu;
    }
    public void setEqpt_eiu(String eqpt_eiu) {
        this.eqpt_eiu = eqpt_eiu;
    }
    public Integer getLast_hb() {
        return last_hb;
    }
    public void setLast_hb(Integer last_hb) {
        this.last_hb = last_hb;
    }
    public Date getLast_comm() {
        return last_comm;
    }
    public void setLast_comm(Date last_comm) {
        this.last_comm = last_comm;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eqpt_id == null) ? 0 : eqpt_id.hashCode());
        result = prime * result + ((eqpt_name == null) ? 0 : eqpt_name.hashCode());
        result = prime * result + ((eqpt_eiu == null) ? 0 : eqpt_eiu.hashCode());
        result = prime * result + ((last_hb == null) ? 0 : last_hb.hashCode());
        result = prime * result + ((last_comm == null) ? 0 : last_comm.hashCode());
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
        equipment_info other = (equipment_info) obj;
        if (eqpt_id == null) {
            if (other.eqpt_id != null)
                return false;
        } else if (!eqpt_id.equals(other.eqpt_id))
            return false;
        if (eqpt_name == null) {
            if (other.eqpt_name != null)
                return false;
        } else if (!eqpt_name.equals(other.eqpt_name))
            return false;
        if (eqpt_eiu == null) {
            if (other.eqpt_eiu != null)
                return false;
        } else if (!eqpt_eiu.equals(other.eqpt_eiu))
            return false;
        if (last_hb == null) {
            if (other.last_hb != null)
                return false;
        } else if (!last_hb.equals(other.last_hb))
            return false;
        if (last_comm == null) {
            if (other.last_comm != null)
                return false;
        } else if (!last_comm.equals(other.last_comm))
            return false;
        return true;
    }

}
