package module.message;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.annotations.SerializedName;

import module.config.ConfigWago;
import module.entities.embeddedPK;
import module.entities.jc_rst_alrm;
import module.entities.jc_rst_alrm_r;
import module.process.Wago;

public class JcRstAlrm {
    private static Logger logger = Logger.getLogger(JcUpdSt.class.getName());
    private MsgHeader msgHeader;

    @SerializedName("jc_id")
    private int jc_id;

    @SerializedName("jc_m")
    private String jc_m;

    @SerializedName("num_of_faults")
    private int num_of_faults;

    @SerializedName("fault_code_desc_list")
    private List<?> fault_code_desc_list;

    private int fault_code;
    
    private String subcompo_m;

    private String active_i;

    public MsgHeader getMsgHeader() {
        return msgHeader;
    }

    public void setMsgHeader(MsgHeader msgHeader) {
        this.msgHeader = msgHeader;
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

    public int getNum_of_faults() {
        return num_of_faults;
    }

    public void setNum_of_faults(int num_of_faults) {
        this.num_of_faults = num_of_faults;
    }

    public List<?> getFault_code_desc_list() {
        return fault_code_desc_list;
    }

    public void setFault_code_desc_list(List<?> fault_code_desc_list) {
        this.fault_code_desc_list = fault_code_desc_list;
    }

    public int getFault_code() {
        return fault_code;
    }

    public void setFault_code(int fault_code) {
        this.fault_code = fault_code;
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
    
    public jc_rst_alrm generateJcRstAlrm() {
        embeddedPK jcRstAlrmPk = new embeddedPK();
        jcRstAlrmPk.setEvent_dt(msgHeader.getEventDt());
        jcRstAlrmPk.setTrans_id(msgHeader.getTransId());

        jc_rst_alrm jcRstAlrm = new jc_rst_alrm();
        jcRstAlrm.setJc_rst_alrm_pk(jcRstAlrmPk);
        jcRstAlrm.setSource_m(msgHeader.getSourceM());
        jcRstAlrm.setEvent_id(msgHeader.getEventId());
        jcRstAlrm.setJc_id(jc_id);
        jcRstAlrm.setJc_m(jc_m);
        jcRstAlrm.setNum_of_faults(num_of_faults);
        jcRstAlrm.setFault_code_desc_list(fault_code_desc_list.toString());
        jcRstAlrm.setTimestamp(new Date());

        return jcRstAlrm;
    }

    public jc_rst_alrm generateJcRstAlrm(int num_of_faults, List<?> fault_code_desc_list) {
        embeddedPK jcRstAlrmPk = new embeddedPK();
        jcRstAlrmPk.setEvent_dt(msgHeader.getEventDt());
        jcRstAlrmPk.setTrans_id(msgHeader.getTransId());

        jc_rst_alrm jcRstAlrm = new jc_rst_alrm();
        jcRstAlrm.setJc_rst_alrm_pk(jcRstAlrmPk);
        jcRstAlrm.setSource_m(msgHeader.getSourceM());
        jcRstAlrm.setEvent_id(msgHeader.getEventId());
        jcRstAlrm.setJc_id(jc_id);
        jcRstAlrm.setJc_m(jc_m);
        jcRstAlrm.setNum_of_faults(num_of_faults);
        jcRstAlrm.setFault_code_desc_list(fault_code_desc_list.toString());
        jcRstAlrm.setTimestamp(new Date());

        return jcRstAlrm;
    }

    public jc_rst_alrm_r generateJcRstAlrmR(String errorC, String errorTxt, int num_of_faults, List<?> fault_code_desc_list) {
        embeddedPK jcRstAlrmRPk = new embeddedPK();
        jcRstAlrmRPk.setEvent_dt(msgHeader.getEventDt());
        jcRstAlrmRPk.setTrans_id(msgHeader.getTransId());

        jc_rst_alrm_r jcRstAlrmR = new jc_rst_alrm_r();
        jcRstAlrmR.setJc_rst_alrm_r_pk(jcRstAlrmRPk);
        jcRstAlrmR.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcRstAlrmR.setEvent_id(msgHeader.getEventId());
        jcRstAlrmR.setError_c(errorC);
        jcRstAlrmR.setError_txt(errorTxt);
        jcRstAlrmR.setJc_id(jc_id);
        jcRstAlrmR.setJc_m(jc_m);
        jcRstAlrmR.setNum_of_faults(num_of_faults);
        jcRstAlrmR.setFault_code_desc_list(fault_code_desc_list.toString());
        jcRstAlrmR.setTimestamp(new Date());

        return jcRstAlrmR;
    }

    public jc_rst_alrm_r generateJcRstAlrmR(String errorC, String errorTxt) {
        embeddedPK jcRstAlrmRPk = new embeddedPK();
        jcRstAlrmRPk.setEvent_dt(msgHeader.getEventDt());
        jcRstAlrmRPk.setTrans_id(msgHeader.getTransId());

        jc_rst_alrm_r jcRstAlrmR = new jc_rst_alrm_r();
        jcRstAlrmR.setJc_rst_alrm_r_pk(jcRstAlrmRPk);
        jcRstAlrmR.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcRstAlrmR.setEvent_id(msgHeader.getEventId());
        jcRstAlrmR.setError_c(errorC);
        jcRstAlrmR.setError_txt(errorTxt);
        jcRstAlrmR.setJc_id(jc_id);
        jcRstAlrmR.setJc_m(jc_m);
        jcRstAlrmR.setNum_of_faults(num_of_faults);
        jcRstAlrmR.setFault_code_desc_list(fault_code_desc_list.toString());
        jcRstAlrmR.setTimestamp(new Date());

        return jcRstAlrmR;
    }

    public Wago generateWago(Wago wago) {
        try{
            wago.setCmdType("JC_RST_ALRM");
            //pubInstructionToWago
            wago.setJcRst("true");
        }catch(Exception e){
            wago.setWagoStatusCode(309);
            logger.error("JcRstAlrm - generateWago : Unkown error");
        }
        return wago;
    }

    public String generateFaultMsg(jc_rst_alrm_r jcRstAlrmR) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\": {\"source_m\": \"" + jcRstAlrmR.getSource_m() + 
                        "\", \"event_dt\": \"" + jcRstAlrmR.getJc_rst_alrm_r_pk().getEvent_dt() + 
                        "\", \"event_id\": \"" + jcRstAlrmR.getEvent_id() + 
                        "\", \"trans_id\": \"" + jcRstAlrmR.getJc_rst_alrm_r_pk().getTrans_id() + 
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\": {\"error_c\": \"" + jcRstAlrmR.getError_c() +
                "\", \"error_txt\": \""+ jcRstAlrmR.getError_txt() +
                "\", \"jc_id\": \"" + jcRstAlrmR.getJc_id() + 
                "\", \"jc_m\": \"" + jcRstAlrmR.getJc_m() + 
                "\", \"num_of_faults\": " + jcRstAlrmR.getNum_of_faults() +
                ", \"fault_code_desc_list\": " + jcRstAlrmR.getFault_code_desc_list() + "}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

}
