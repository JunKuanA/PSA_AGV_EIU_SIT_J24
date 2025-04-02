package module.message;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import module.config.ConfigWago;
import module.entities.embeddedPK;
import module.entities.jc_alrm_info;
import module.utils.Utils;

public class JcAlrmInfo {
    // private static Logger logger = Logger.getLogger(JcUpdSt.class.getName());
    private MsgHeader msgHeader;

    @SerializedName("jc_id")
    private int jc_id;

    @SerializedName("jc_m")
    private String jc_m;

    @SerializedName("mi_i")
    private String mi_i;

    @SerializedName("alert_mode")
    private String alert_mode;

    @SerializedName("num_of_faults")
    private int num_of_faults;

    @SerializedName("fault_code_desc_list")
    private List<?> fault_code_desc_list;

    public JcAlrmInfo() {
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

    public String getAlert_mode() {
        return alert_mode;
    }

    public void setAlert_mode(String alert_mode) {
        this.alert_mode = alert_mode;
    }

    public String getJc_m() {
        return jc_m;
    }

    public void setJc_m(String jc_m) {
        this.jc_m = jc_m;
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

    public List<?> getFault_code_desc_list() {
        return fault_code_desc_list;
    }

    public void setFault_code_desc_list(List<?> fault_code_desc_list) {
        this.fault_code_desc_list = fault_code_desc_list;
    }


    public jc_alrm_info generateJcAlrmInfo(String mi_i, String alert_mode, int num_of_faults, List<?> fault_code_desc_list){
        embeddedPK jcAlrmInfoPk = new embeddedPK();
        jcAlrmInfoPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcAlrmInfoPk.setTrans_id(ConfigWago.getEsbJcAlrmInfo().toUpperCase());

        jc_alrm_info jcAlrmInfo = new jc_alrm_info();
        jcAlrmInfo.setJc_alrm_info_pk(jcAlrmInfoPk);
        jcAlrmInfo.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcAlrmInfo.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcAlrmInfo.setJc_id(jc_id);
        jcAlrmInfo.setJc_m(jc_m);
        jcAlrmInfo.setAlert_mode(alert_mode);
        jcAlrmInfo.setMi_i(mi_i);
        jcAlrmInfo.setNum_of_faults(num_of_faults);
        jcAlrmInfo.setFault_code_desc_list(fault_code_desc_list.toString());
        jcAlrmInfo.setTimestamp(new Date());

        return jcAlrmInfo;
    }

    public String generateReturnMsg(jc_alrm_info jcAlrmInfo) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcAlrmInfo.getSource_m() +
                        "\",\"event_dt\":\"" + jcAlrmInfo.getJc_alrm_info_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcAlrmInfo.getEvent_id() +
                        "\",\"trans_id\":\"" + jcAlrmInfo.getJc_alrm_info_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"jc_id\":" + jcAlrmInfo.getJc_id() +
                ",\"jc_m\":\"" + jcAlrmInfo.getJc_m() +
                "\",\"alert_mode\":\"" + jcAlrmInfo.getAlert_mode() +
                "\",\"mi_i\":" + jcAlrmInfo.getMi_i() +
                ",\"num_of_faults\":" + jcAlrmInfo.getNum_of_faults() +
                ",\"fault_code_desc_list\":" + jcAlrmInfo.getFault_code_desc_list() +"}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }
}
