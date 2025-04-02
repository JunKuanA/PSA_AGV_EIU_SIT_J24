package module.message;

import java.util.Date;
import java.util.List;

import module.utils.Utils;
import org.apache.log4j.Logger;

import com.google.gson.annotations.SerializedName;

import module.config.ConfigWago;
import module.entities.embeddedPK;
import module.entities.jc_enq_info;
import module.entities.jc_enq_info_r;
import module.process.Wago;

public class JcEnqInfo {
    private static Logger logger = Logger.getLogger(JcUpdSt.class.getName());
    private MsgHeader msgHeader;

    @SerializedName("jc_id")
    private int jc_id;

    @SerializedName("jc_m")
    private String jc_m;

    @SerializedName("jc_status_c")
    private String jc_status_c;

    @SerializedName("alert_mode")
    private String alert_mode;

    @SerializedName("mi_i")
    private String mi_i;

    @SerializedName("control_mode")
    private String control_mode;

    @SerializedName("barriers_down_i")
    private String barriers_down_i;

    @SerializedName("traffic_light_i")
    private String traffic_light_i;

    @SerializedName("mnl_xing_req_i")
    private String mnl_xing_req_i;

    @SerializedName("num_of_pm_loop")
    private int num_of_pm_loop;

    @SerializedName("pm_loop_list")
    private List<?> pm_loop_list;

    private String pm_loop_m;

    private String pm_active_i;

    @SerializedName("num_of_uturn_loop")
    private int num_of_uturn_loop;

    @SerializedName("uturn_loop_list")
    private List<?> uturn_loop_list;

    private String uturn_loop_m;

    private String uturn_active_i;

    @SerializedName("num_of_faults")
    private int num_of_faults;

    @SerializedName("fault_code_desc_list")
    private List<?> fault_code_desc_list;

    public JcEnqInfo() {
    }

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

    public List<?> getPm_loop_list() {
        return pm_loop_list;
    }

    public void setPm_loop_list(List<?> pm_loop_list) {
        this.pm_loop_list = pm_loop_list;
    }

    public String getPm_loop_m() {
        return pm_loop_m;
    }

    public void setPm_loop_m(String pm_loop_m) {
        this.pm_loop_m = pm_loop_m;
    }

    public String getPm_active_i() {
        return pm_active_i;
    }

    public void setPm_active_i(String pm_active_i) {
        this.pm_active_i = pm_active_i;
    }

    public int getNum_of_uturn_loop() {
        return num_of_uturn_loop;
    }

    public void setNum_of_uturn_loop(int num_of_uturn_loop) {
        this.num_of_uturn_loop = num_of_uturn_loop;
    }

    public List<?> getUturn_loop_list() {
        return uturn_loop_list;
    }

    public void setUturn_loop_list(List<?> uturn_loop_list) {
        this.uturn_loop_list = uturn_loop_list;
    }

    public String getUturn_loop_m() {
        return uturn_loop_m;
    }

    public void setUturn_loop_m(String uturn_loop_m) {
        this.uturn_loop_m = uturn_loop_m;
    }

    public String getUturn_active_i() {
        return uturn_active_i;
    }

    public void setUturn_active_i(String uturn_active_i) {
        this.uturn_active_i = uturn_active_i;
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

    public jc_enq_info generateJcEnqInfo(){
        embeddedPK jcEnqInfoPk = new embeddedPK();
        jcEnqInfoPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcEnqInfoPk.setTrans_id(ConfigWago.getEsbJcEnqInfo().toUpperCase());

        jc_enq_info jcEnqInfo = new jc_enq_info();
        jcEnqInfo.setJc_enq_info_pk(jcEnqInfoPk);
        jcEnqInfo.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcEnqInfo.setEvent_id(ConfigWago.getJcM()+"-"+ Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcEnqInfo.setJc_id(jc_id);
        jcEnqInfo.setJc_m(jc_m);
        jcEnqInfo.setMnl_xing_req_i(mnl_xing_req_i);
        // jcEnqInfo.setJc_status_c(jc_status_c);
        // jcEnqInfo.setMi_i(mi_i);
        // jcEnqInfo.setControl_mode(control_mode);
        // jcEnqInfo.setNum_of_pm_loop(num_of_pm_loop);
        // jcEnqInfo.setPm_loop_list(pm_loop_list.toString());
        // jcEnqInfo.setNum_of_uturn_loop(num_of_uturn_loop);
        // jcEnqInfo.setUturn_loop_list(uturn_loop_list.toString());
        // jcEnqInfo.setNum_of_faults(num_of_faults);
        // jcEnqInfo.setFault_code_desc_list(fault_code_desc_list.toString());
        jcEnqInfo.setTimestamp(new Date());

        return jcEnqInfo;
    }

    // public jc_enq_info generateJcEnqInfo(String jc_status_c, String mi_i, String control_mode, int num_of_pm_loop, List<?> pm_loop_list, int num_of_uturn_loop, 
    //                                     List<?> uturn_loop_list, int num_of_faults, List<?> fault_code_desc_list){
    //     embeddedPK jcEnqInfoPk = new embeddedPK();
    //     jcEnqInfoPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
    //     jcEnqInfoPk.setTrans_id(ConfigWago.getEsbJcEnqInfo().toUpperCase());

    //     jc_enq_info jcEnqInfo = new jc_enq_info();
    //     jcEnqInfo.setJc_enq_info_pk(jcEnqInfoPk);
    //     jcEnqInfo.setSource_m(ConfigWago.getSourceM().toUpperCase());
    //     jcEnqInfo.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
    //     jcEnqInfo.setJc_id(jc_id);
    //     jcEnqInfo.setJc_m(jc_m);
    //     jcEnqInfo.setJc_status_c(jc_status_c);
    //     jcEnqInfo.setMi_i(mi_i);
    //     jcEnqInfo.setControl_mode(control_mode);
    //     jcEnqInfo.setNum_of_pm_loop(num_of_pm_loop);
    //     jcEnqInfo.setPm_loop_list(pm_loop_list.toString());
    //     jcEnqInfo.setNum_of_uturn_loop(num_of_uturn_loop);
    //     jcEnqInfo.setUturn_loop_list(uturn_loop_list.toString());
    //     jcEnqInfo.setNum_of_faults(num_of_faults);
    //     jcEnqInfo.setFault_code_desc_list(fault_code_desc_list.toString());
    //     jcEnqInfo.setTimestamp(new Date());

    //     return jcEnqInfo;
    // }

    public jc_enq_info_r generateJcEnqInfoR(){
        embeddedPK jcEnqInfoRPk = new embeddedPK();
        jcEnqInfoRPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcEnqInfoRPk.setTrans_id(ConfigWago.getEsbJcEnqInfoR().toUpperCase());

        jc_enq_info_r jcEnqInfoR = new jc_enq_info_r();
        jcEnqInfoR.setJc_enq_info_r_pk(jcEnqInfoRPk);
        jcEnqInfoR.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcEnqInfoR.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcEnqInfoR.setError_c(msgHeader.getErrorC());
        jcEnqInfoR.setError_txt(msgHeader.getErrorTxt());
        jcEnqInfoR.setJc_id(jc_id);
        jcEnqInfoR.setJc_m(jc_m);
        jcEnqInfoR.setJc_status_c(jc_status_c);
        jcEnqInfoR.setAlert_mode(alert_mode);
        jcEnqInfoR.setMi_i(mi_i);
        jcEnqInfoR.setControl_mode(control_mode);
        jcEnqInfoR.setBarriers_down_i(barriers_down_i);
        jcEnqInfoR.setTraffic_light_i(traffic_light_i);
        jcEnqInfoR.setMnl_xing_req_i(mnl_xing_req_i);
        jcEnqInfoR.setNum_of_pm_loop(num_of_pm_loop);
        jcEnqInfoR.setPm_loop_list(pm_loop_list.toString());
        jcEnqInfoR.setNum_of_uturn_loop(num_of_uturn_loop);
        jcEnqInfoR.setUturn_loop_list(uturn_loop_list.toString());
        jcEnqInfoR.setNum_of_faults(num_of_faults);
        jcEnqInfoR.setFault_code_desc_list(fault_code_desc_list.toString());
        jcEnqInfoR.setTimestamp(new Date());

        return jcEnqInfoR;
    }

    public void setInfo(JcUpdSt jcUpdStM){
        msgHeader = jcUpdStM.getMsgHeader();
        jc_m = jcUpdStM.getJc_m();
        jc_id = jcUpdStM.getJc_id();
    }

    public jc_enq_info_r generateJcEnqInfoR(String errorC, String errorTxt, String jc_status_c, String alert_mode, String mi_i, String control_mode, String barriers_down_i, String traffic_light_i, String mnl_xing_req_i, int num_of_pm_loop, 
                                            List<?> pm_loop_list, int num_of_uturn_loop, List<?> uturn_loop_list, int num_of_faults, List<?> fault_code_desc_list){
        embeddedPK jcEnqInfoRPk = new embeddedPK();
        jcEnqInfoRPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcEnqInfoRPk.setTrans_id(ConfigWago.getEsbJcEnqInfoR().toUpperCase());

        jc_enq_info_r jcEnqInfoR = new jc_enq_info_r();
        jcEnqInfoR.setJc_enq_info_r_pk(jcEnqInfoRPk);
        jcEnqInfoR.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcEnqInfoR.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcEnqInfoR.setError_c(errorC);
        jcEnqInfoR.setError_txt(errorTxt);
        jcEnqInfoR.setJc_id(jc_id);
        jcEnqInfoR.setJc_m(jc_m);
        jcEnqInfoR.setJc_status_c(jc_status_c);
        jcEnqInfoR.setAlert_mode(alert_mode);
        jcEnqInfoR.setMi_i(mi_i);
        jcEnqInfoR.setControl_mode(control_mode);
        jcEnqInfoR.setBarriers_down_i(barriers_down_i);
        jcEnqInfoR.setTraffic_light_i(traffic_light_i);
        jcEnqInfoR.setMnl_xing_req_i(mnl_xing_req_i);
        jcEnqInfoR.setNum_of_pm_loop(num_of_pm_loop);
        jcEnqInfoR.setPm_loop_list(pm_loop_list.toString());
        jcEnqInfoR.setNum_of_uturn_loop(num_of_uturn_loop);
        jcEnqInfoR.setUturn_loop_list(uturn_loop_list.toString());
        jcEnqInfoR.setNum_of_faults(num_of_faults);
        jcEnqInfoR.setFault_code_desc_list(fault_code_desc_list.toString());
        jcEnqInfoR.setTimestamp(new Date());

        return jcEnqInfoR;
    }

    public jc_enq_info_r generateJcEnqInfoR(String errorC, String errorTxt){
        embeddedPK jcEnqInfoRPk = new embeddedPK();
        jcEnqInfoRPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcEnqInfoRPk.setTrans_id(ConfigWago.getEsbJcEnqInfoR().toUpperCase());

        jc_enq_info_r jcEnqInfoR = new jc_enq_info_r();
        jcEnqInfoR.setJc_enq_info_r_pk(jcEnqInfoRPk);
        jcEnqInfoR.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcEnqInfoR.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcEnqInfoR.setError_c(errorC);
        jcEnqInfoR.setError_txt(errorTxt);
        jcEnqInfoR.setJc_id(jc_id);
        jcEnqInfoR.setJc_m(jc_m);
        jcEnqInfoR.setJc_status_c(jc_status_c);
        jcEnqInfoR.setAlert_mode(alert_mode);
        jcEnqInfoR.setMi_i(mi_i);
        jcEnqInfoR.setControl_mode(control_mode);
        jcEnqInfoR.setBarriers_down_i(barriers_down_i);
        jcEnqInfoR.setTraffic_light_i(traffic_light_i);
        jcEnqInfoR.setMnl_xing_req_i(mnl_xing_req_i);
        jcEnqInfoR.setNum_of_pm_loop(num_of_pm_loop);
        jcEnqInfoR.setPm_loop_list(pm_loop_list.toString());
        jcEnqInfoR.setNum_of_uturn_loop(num_of_uturn_loop);
        jcEnqInfoR.setUturn_loop_list(uturn_loop_list.toString());
        jcEnqInfoR.setNum_of_faults(num_of_faults);
        jcEnqInfoR.setFault_code_desc_list(fault_code_desc_list.toString());
        jcEnqInfoR.setTimestamp(new Date());

        return jcEnqInfoR;
    }

    public String generateReturnMsg(jc_enq_info_r jcEnqInfoR) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcEnqInfoR.getSource_m() +
                        "\",\"event_dt\":\"" + jcEnqInfoR.getJc_enq_info_r_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcEnqInfoR.getEvent_id() +
                        "\",\"trans_id\":\"" + jcEnqInfoR.getJc_enq_info_r_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"error_c\":\"OK" +
                "\",\"error_txt\":\""+
                "\",\"jc_id\":" + jcEnqInfoR.getJc_id() +
                ",\"jc_m\":\"" + jcEnqInfoR.getJc_m() +
                "\",\"jc_status_c\":\"" + jcEnqInfoR.getJc_status_c() +
                "\",\"alert_mode\":\"OK" +
                "\",\"mi_i\":false" +
                ",\"control_mode\":\"" + jcEnqInfoR.getControl_mode() +
                "\",\"barriers_down_i\":" + jcEnqInfoR.getBarriers_down_i() +
                ",\"traffic_light_i\":" + jcEnqInfoR.getTraffic_light_i() +
                ",\"mnl_xing_req_i\":" + jcEnqInfoR.getMnl_xing_req_i() +
                ",\"num_of_pm_loop\":" + jcEnqInfoR.getNum_of_pm_loop() +
                ",\"pm_loop_list\":" + jcEnqInfoR.getPm_loop_list() +
                ",\"num_of_uturn_loop\":" + jcEnqInfoR.getNum_of_uturn_loop() +
                ",\"uturn_loop_list\":" + jcEnqInfoR.getUturn_loop_list() +
                ",\"num_of_faults\":0" +
                ",\"fault_code_desc_list\":[{}]}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

    public String generateFaultMsg(jc_enq_info_r jcEnqInfoR) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcEnqInfoR.getSource_m() +
                        "\",\"event_dt\":\"" + jcEnqInfoR.getJc_enq_info_r_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcEnqInfoR.getEvent_id() +
                        "\",\"trans_id\":\"" + jcEnqInfoR.getJc_enq_info_r_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"error_c\":\"" + jcEnqInfoR.getError_c() +
                "\",\"error_txt\":\""+ jcEnqInfoR.getError_txt() +
                "\",\"jc_id\":" + jcEnqInfoR.getJc_id() +
                ",\"jc_m\":\"" + jcEnqInfoR.getJc_m() +
                "\",\"jc_status_c\":\"" + jcEnqInfoR.getJc_status_c() +
                "\",\"alert_mode\":\"" + jcEnqInfoR.getAlert_mode() +
                "\",\"mi_i\":" + jcEnqInfoR.getMi_i() +
                ",\"control_mode\":\"" + jcEnqInfoR.getControl_mode() +
                "\",\"barriers_down_i\":" + jcEnqInfoR.getBarriers_down_i() +
                ",\"traffic_light_i\":" + jcEnqInfoR.getTraffic_light_i() +
                ",\"mnl_xing_req_i\":" + jcEnqInfoR.getMnl_xing_req_i() +
                ",\"num_of_pm_loop\":" + jcEnqInfoR.getNum_of_pm_loop() +
                ",\"pm_loop_list\":" + jcEnqInfoR.getPm_loop_list() +
                ",\"num_of_uturn_loop\":" + jcEnqInfoR.getNum_of_uturn_loop() +
                ",\"uturn_loop_list\":" + jcEnqInfoR.getUturn_loop_list() +
                ",\"num_of_faults\":" + jcEnqInfoR.getNum_of_faults() +
                ",\"fault_code_desc_list\":" + jcEnqInfoR.getFault_code_desc_list() + "}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

    public Wago generateWago(Wago wago) {
        /*
         * send command via OPCUA
         */
        try{
            wago.setCmdType("JC_ENQ_INFO");
        }catch(Exception e){
            wago.setWagoStatusCode(309);
            logger.error("JcEnqInfo - generateWago: Unkown error");
        }
        return wago;
    }
}
