package module.message;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import module.config.ConfigWago;
import module.entities.embeddedPK;
import module.entities.jc_uturn_dtec_info;
import module.utils.Utils;

public class JcUturnDtecInfo {
    // private static Logger logger = Logger.getLogger(JcUpdSt.class.getName());
    private MsgHeader msgHeader;

    @SerializedName("jc_id")
    private int jc_id;

    @SerializedName("jc_m")
    private String jc_m;

    @SerializedName("num_of_uturn_loop")
    private int num_of_uturn_loop;

    @SerializedName("uturn_loop_list")
    private List<?> uturn_loop_list;

    public JcUturnDtecInfo() {
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

    public jc_uturn_dtec_info generateJcUturnDtecInfo(int num_of_uturn_loop, List<?> uturn_loop_list){
        embeddedPK jcUturnDtecInfoPk = new embeddedPK();
        jcUturnDtecInfoPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcUturnDtecInfoPk.setTrans_id(ConfigWago.getEsbJcUturnDtecInfo().toUpperCase());

        jc_uturn_dtec_info jcUturnDtecInfo = new jc_uturn_dtec_info();
        jcUturnDtecInfo.setJc_uturn_dtec_info_pk(jcUturnDtecInfoPk);
        jcUturnDtecInfo.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcUturnDtecInfo.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcUturnDtecInfo.setJc_id(jc_id);
        jcUturnDtecInfo.setJc_m(jc_m);
        jcUturnDtecInfo.setNum_of_uturn_loop(num_of_uturn_loop);
        jcUturnDtecInfo.setUturn_loop_list(uturn_loop_list.toString());
        jcUturnDtecInfo.setTimestamp(new Date());

        return jcUturnDtecInfo;
    }

    public String generateReturnMsg(jc_uturn_dtec_info jcUturnDtecInfo) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcUturnDtecInfo.getSource_m() +
                        "\",\"event_dt\":\"" + jcUturnDtecInfo.getJc_uturn_dtec_info_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcUturnDtecInfo.getEvent_id() +
                        "\",\"trans_id\":\"" + jcUturnDtecInfo.getJc_uturn_dtec_info_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"jc_id\":" + jcUturnDtecInfo.getJc_id() +
                ",\"jc_m\":\"" + jcUturnDtecInfo.getJc_m() +
                "\",\"num_of_uturn_loop\":" + jcUturnDtecInfo.getNum_of_uturn_loop() +
                ",\"uturn_loop_list\":" + jcUturnDtecInfo.getUturn_loop_list() + "}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
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

    
}
