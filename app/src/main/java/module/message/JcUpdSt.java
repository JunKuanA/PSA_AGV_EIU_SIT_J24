package module.message;

import java.util.Date;

import org.apache.log4j.Logger;

import com.google.gson.annotations.SerializedName;

import module.config.ConfigWago;
import module.entities.embeddedPK;
import module.entities.jc_upd_st;
import module.entities.jc_upd_st_r;
import module.process.Wago;
import module.utils.Utils;

public class JcUpdSt {
    private static Logger logger = Logger.getLogger(JcUpdSt.class.getName());
    private MsgHeader msgHeader;

    @SerializedName("jc_id")
    private int jc_id;

    @SerializedName("jc_m")
    private String jc_m;

    @SerializedName("new_jc_status_c")
    private String new_jc_status_c;

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

    public String getNew_jc_status_c() {
        return new_jc_status_c;
    }

    public void setNew_jc_status_c(String new_jc_status_c) {
        this.new_jc_status_c = new_jc_status_c;
    }

    public jc_upd_st generateJcUpdSt() {
        embeddedPK jcUpdStPk = new embeddedPK();
        jcUpdStPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcUpdStPk.setTrans_id(ConfigWago.getEsbJcUpdStR().toUpperCase());

        jc_upd_st jcUpdSt = new jc_upd_st();
        jcUpdSt.setJc_upd_st_pk(jcUpdStPk);
        jcUpdSt.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcUpdSt.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcUpdSt.setJc_id(jc_id);
        jcUpdSt.setJc_m(jc_m);
        jcUpdSt.setNew_jc_status_c(new_jc_status_c);
        jcUpdSt.setTimestamp(new Date());

        return jcUpdSt;
    }

    public jc_upd_st_r generateJcUpdStR(String errorC, String errorTxt, String currJcStatusC) {
        embeddedPK jcUpdStRPk = new embeddedPK();
        jcUpdStRPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        jcUpdStRPk.setTrans_id(ConfigWago.getEsbJcUpdStR().toUpperCase());

        jc_upd_st_r jcUpdStR = new jc_upd_st_r();
        jcUpdStR.setJc_upd_st_r_pk(jcUpdStRPk);
        jcUpdStR.setSource_m(ConfigWago.getSourceM().toUpperCase());
        jcUpdStR.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        jcUpdStR.setError_c(errorC);
        jcUpdStR.setError_txt(errorTxt);
        jcUpdStR.setJc_id(jc_id);
        jcUpdStR.setJc_m(jc_m);
        jcUpdStR.setCurr_jc_status_c(currJcStatusC);
        jcUpdStR.setTimestamp(new Date());

        return jcUpdStR;
    }

    public void setAckRInfo(JcUpdSt jcUpdStM){
        msgHeader = jcUpdStM.getMsgHeader();
        jc_m = jcUpdStM.getJc_m();
        jc_id = jcUpdStM.getJc_id();
    }

    public Wago generateWago(Wago wago) {
        /*
         * send command via OPCUA
         */
        try{
            wago.setCmdType("JC_UPD_ST");
            //pubInstructionToWago
            if(new_jc_status_c.equalsIgnoreCase("PM_GREEN")){//Red Light Cmd
                wago.setNewJcSt("PM_GREEN");
            }else if(new_jc_status_c.equalsIgnoreCase("PM_RED")){//Green Light Cmd
                wago.setNewJcSt("PM_RED");
            }else{
                //Wrong Cmd, set error code and return to PSA Solace
                wago.setWagoStatusCode(300);
                logger.debug(String.format("JcUpdSt - generateWago - (messageArrived): newJcStatus is wrong : %s", new_jc_status_c));
            }
        }catch(Exception e){
            wago.setWagoStatusCode(309);
            logger.error("JcUpdSt - generateWago: Unkown error");
        }
        return wago;
    }

    public Wago generateAckWago(Wago wago) {
        /*
         * send command via OPCUA
         */
        try{
            wago.setCmdType("JC_ACK_R");
        }catch(Exception e){
            wago.setWagoStatusCode(309);
            logger.error("JcUpdSt - generateWago: Unkown error");
        }
        return wago;
    }

    public String generateFaultMsg(jc_upd_st_r jcUpdStR) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcUpdStR.getSource_m() +
                        "\",\"event_dt\":\"" + jcUpdStR.getJc_upd_st_r_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcUpdStR.getEvent_id() +
                        "\",\"trans_id\":\"" + jcUpdStR.getJc_upd_st_r_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"error_c\":\"Failed" +
                "\",\"error_txt\":\""+ jcUpdStR.getError_txt() +
                "\",\"jc_id\":" + jcUpdStR.getJc_id() +
                ",\"jc_m\":\"" + jcUpdStR.getJc_m() +
                "\",\"curr_jc_status_c\":\"" + jcUpdStR.getCurr_jc_status_c() + "\"}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

    public String generateReturnMsg(jc_upd_st_r jcUpdStR) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + jcUpdStR.getSource_m() +
                        "\",\"event_dt\":\"" + jcUpdStR.getJc_upd_st_r_pk().getEvent_dt() +
                        "\",\"event_id\":\"" + jcUpdStR.getEvent_id() +
                        "\",\"trans_id\":\"" + jcUpdStR.getJc_upd_st_r_pk().getTrans_id() +
                        "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"error_c\":\"" + jcUpdStR.getError_c() +
                "\",\"error_txt\":\"" + jcUpdStR.getError_txt() +
                "\",\"jc_id\":" + jcUpdStR.getJc_id() +
                ",\"jc_m\":\"" + jcUpdStR.getJc_m() +
                "\",\"curr_jc_status_c\":\"" + jcUpdStR.getCurr_jc_status_c() + "\"}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

}
