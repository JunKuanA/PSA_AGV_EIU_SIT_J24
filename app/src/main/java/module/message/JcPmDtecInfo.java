package module.message;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import module.config.ConfigWago;
import module.entities.embeddedPK;
import module.entities.jc_pm_dtec_info;
import module.utils.Utils;

public class JcPmDtecInfo {
    // private static Logger logger = Logger.getLogger(JcUpdSt.class.getName());
    private MsgHeader msgHeader;

    @SerializedName("jc_id")
    private int jc_id;

    @SerializedName("jc_m")
    private String jc_m;

    @SerializedName("num_of_pm_loop")
    private int num_of_pm_loop;

    @SerializedName("pm_loop_list")
    private List<?> pm_loop_list;

    public JcPmDtecInfo() {
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

    public jc_pm_dtec_info generateJcPmDtecInfo(int num_of_pm_loop, List<?> pm_loop_list){
        embeddedPK jcPmDtecInfoPk = new embeddedPK();
        jcPmDtecInfoPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcPmDtecInfoPk.setTrans_id(ConfigWago.getEsbJcPmDtecInfo().toUpperCase());

        jc_pm_dtec_info jcPmDtecInfo = new jc_pm_dtec_info();
        jcPmDtecInfo.setJc_pm_dtec_info_pk(jcPmDtecInfoPk);
        jcPmDtecInfo.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcPmDtecInfo.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        
        jcPmDtecInfo.setJc_id(jc_id);
        jcPmDtecInfo.setJc_m(jc_m);
        jcPmDtecInfo.setNum_of_pm_loop(num_of_pm_loop);
        jcPmDtecInfo.setPm_loop_list(pm_loop_list.toString());
        jcPmDtecInfo.setTimestamp(new Date());

        return jcPmDtecInfo;
    }

    public String generateReturnMsg(jc_pm_dtec_info jcPmDtecInfo) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcPmDtecInfo.getSource_m() +
                        "\",\"event_dt\":\"" + jcPmDtecInfo.getJc_pm_dtec_info_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcPmDtecInfo.getEvent_id() +
                        "\",\"trans_id\":\"" + jcPmDtecInfo.getJc_pm_dtec_info_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"jc_id\":" + jcPmDtecInfo.getJc_id() +
                ",\"jc_m\":\"" + jcPmDtecInfo.getJc_m() +
                "\",\"num_of_pm_loop\":" + jcPmDtecInfo.getNum_of_pm_loop() +
                ",\"pm_loop_list\":" + jcPmDtecInfo.getPm_loop_list() + "}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

    public List<?> getPm_loop_list() {
        return pm_loop_list;
    }

    public void setPm_loop_list(List<?> pm_loop_list) {
        this.pm_loop_list = pm_loop_list;
    }
    public int getNum_of_pm_loop() {
        return num_of_pm_loop;
    }

    public void setNum_of_pm_loop(int num_of_pm_loop) {
        this.num_of_pm_loop = num_of_pm_loop;
    }
}
