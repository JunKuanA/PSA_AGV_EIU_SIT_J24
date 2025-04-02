package module.message;

import java.util.Date;
import com.google.gson.annotations.SerializedName;

import module.config.ConfigWago;
import module.entities.embeddedPK;
import module.entities.jc_hrtbt_info;
import module.utils.Utils;

public class JcHrtbtInfo {
    // private static Logger logger = Logger.getLogger(JcUpdSt.class.getName());
    private MsgHeader msgHeader;

    @SerializedName("jc_id")
    private int jc_id;

    @SerializedName("jc_m")
    private String jc_m;

    @SerializedName("num_of_dead")
    private int num_of_dead;

    @SerializedName("control_mode")
    private String control_mode;

    public JcHrtbtInfo() {
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

    

    public String getControl_mode() {
        return control_mode;
    }

    public void setControl_mode(String control_mode) {
        this.control_mode = control_mode;
    }

    public jc_hrtbt_info generateJcHrtbtInfo(String control_mode){
        embeddedPK jcHrtbtInfoPk = new embeddedPK();
        jcHrtbtInfoPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcHrtbtInfoPk.setTrans_id(ConfigWago.getEsbJcHrtbtInfo().toUpperCase());

        jc_hrtbt_info jcHrtbtInfo = new jc_hrtbt_info();
        jcHrtbtInfo.setJc_hrtbt_info_pk(jcHrtbtInfoPk);
        jcHrtbtInfo.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcHrtbtInfo.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcHrtbtInfo.setJc_id(jc_id);
        jcHrtbtInfo.setJc_m(jc_m);
        jcHrtbtInfo.setControl_mode(control_mode);
        jcHrtbtInfo.setTimestamp(new Date());

        return jcHrtbtInfo;
    }

    public String generateReturnMsg(jc_hrtbt_info jcHrtbtInfo) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcHrtbtInfo.getSource_m() +
                        "\",\"event_dt\":\"" + jcHrtbtInfo.getJc_hrtbt_info_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcHrtbtInfo.getEvent_id() +
                        "\",\"trans_id\":\"" + jcHrtbtInfo.getJc_hrtbt_info_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"jc_id\":" + jcHrtbtInfo.getJc_id() +
                ",\"jc_m\":\"" + jcHrtbtInfo.getJc_m() +
                "\",\"control_mode\":\"" + jcHrtbtInfo.getControl_mode() +
                "\"}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

    public int getNum_of_dead() {
        return num_of_dead;
    }

    public void setNum_of_dead(int num_of_dead) {
        this.num_of_dead = num_of_dead;
    }
}
