package module.message;

import java.util.Date;
import com.google.gson.annotations.SerializedName;

import module.config.ConfigWago;
import module.entities.embeddedPK;
import module.entities.jc_ack_r;
import module.utils.Utils;

public class JcAckR {
    // private static Logger logger = Logger.getLogger(JcUpdSt.class.getName());
    private MsgHeader msgHeader;

    @SerializedName("jc_id")
    private int jc_id;

    @SerializedName("jc_m")
    private String jc_m;

    @SerializedName("prev_trans_id")
    private String prev_trans_id;

    public JcAckR() {
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

    public String getPrev_trans_id() {
        return prev_trans_id;
    }

    public void setPrev_trans_id(String prev_trans_id) {
        this.prev_trans_id = prev_trans_id;
    }

    public jc_ack_r generateJcAckR(String errorC, String errorTxt, String prevtransId) {
        embeddedPK jcAckRPk = new embeddedPK();
        jcAckRPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcAckRPk.setTrans_id(ConfigWago.getEsbJcAckR().toUpperCase());

        jc_ack_r jcAckR = new jc_ack_r();
        jcAckR.setJc_ack_r_pk(jcAckRPk);
        jcAckR.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcAckR.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcAckR.setError_c(errorC);
        jcAckR.setError_txt(errorTxt);
        jcAckR.setJc_id(jc_id);
        jcAckR.setJc_m(jc_m);
        jcAckR.setPrev_trans_id(prevtransId);
        jcAckR.setTimestamp(new Date());

        return jcAckR;
    }

    public String generateAckReturnMsg(jc_ack_r jcAckR) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcAckR.getSource_m() +
                        "\",\"event_dt\":\"" + jcAckR.getJc_ack_r_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcAckR.getEvent_id() +
                        "\",\"trans_id\":\"" + jcAckR.getJc_ack_r_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"error_c\":\"" + jcAckR.getError_c() +
                "\",\"error_txt\":\"" + jcAckR.getError_txt() +
                "\",\"jc_id\":" + jcAckR.getJc_id() +
                ",\"jc_m\":\"" + jcAckR.getJc_m() +
                "\",\"prev_trans_id\":\"" + jcAckR.getPrev_trans_id() + "\"}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

    public String generateFaultMsg(jc_ack_r jcAckR) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcAckR.getSource_m() +
                        "\",\"event_dt\":\"" + jcAckR.getJc_ack_r_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcAckR.getEvent_id() +
                        "\",\"trans_id\":\"" + jcAckR.getJc_ack_r_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"error_c\":\"Failed" +
                "\",\"error_txt\":\"" + jcAckR.getError_txt() +
                "\",\"jc_id\":" + jcAckR.getJc_id() +
                ",\"jc_m\":\"" + jcAckR.getJc_m() +
                "\",\"prev_trans_id\":\"" + jcAckR.getPrev_trans_id() + "\"}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }
}
