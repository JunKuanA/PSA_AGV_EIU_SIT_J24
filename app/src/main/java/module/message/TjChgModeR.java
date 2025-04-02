package module.message;

import java.util.Date;

import module.entities.tj_chg_mode_r;
import org.apache.log4j.Logger;

import com.google.gson.annotations.SerializedName;

import module.config.ConfigWago;
import module.entities.embeddedPK;
import module.process.Wago;
import module.utils.Utils;

public class TjChgModeR {
    private static Logger logger = Logger.getLogger(TjChgModeR.class.getName());
    private MsgHeader msgHeader;

    @SerializedName("tj_id")
    private int tj_id;

    @SerializedName("tj_m")
    private String tj_m;

    @SerializedName("curr_tj_mode_c")
    private String curr_tj_mode_c;

    public MsgHeader getMsgHeader() {
        return msgHeader;
    }

    public void setMsgHeader(MsgHeader msgHeader) {
        this.msgHeader = msgHeader;
    }

    public int getTj_id() {
        return tj_id;
    }

    public void setTj_id(int tj_id) {
        this.tj_id = tj_id;
    }
    public String getTj_m() {
        return tj_m;
    }

    public void setTj_m(String Tj_m) {
        this.tj_m = tj_m;
    }

    public String getCurr_tj_mode_c() {
        return curr_tj_mode_c;
    }

    public void setCurr_tj_mode_c(String curr_tj_mode_c) {
        this.curr_tj_mode_c = curr_tj_mode_c;
    }


    public tj_chg_mode_r generateTjChgModeR() {
        embeddedPK tjChgModeRPk = new embeddedPK();
        tjChgModeRPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        tjChgModeRPk.setTrans_id(ConfigWago.getEsbTjChgModeR().toUpperCase());

        tj_chg_mode_r tjChgModeR = new tj_chg_mode_r();
        tjChgModeR.setTj_chg_mode_r_pk(tjChgModeRPk);
        tjChgModeR.setSource_m(ConfigWago.getSourceM().toUpperCase());
        tjChgModeR.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        tjChgModeR.setTj_id(tj_id);
        tjChgModeR.setTj_m(tj_m);
        tjChgModeR.setNew_tj_mode_c(curr_tj_mode_c);
        tjChgModeR.setTimestamp(new Date());

        return tjChgModeR;
    }

    public tj_chg_mode_r generateTjChgModeR(String errorC, String errorTxt, String currtjmodec) {
        embeddedPK tjChgModeRPk = new embeddedPK();
        tjChgModeRPk.setEvent_dt(Utils.getCurrentDateFormat(ConfigWago.getEventdttimeformat()));
        tjChgModeRPk.setTrans_id(ConfigWago.getEsbJcHrtbtInfo().toUpperCase());

        tj_chg_mode_r tjChgModeR = new tj_chg_mode_r();
        tjChgModeR.setTj_chg_mode_r_pk(tjChgModeRPk);
        tjChgModeR.setSource_m(ConfigWago.getSourceM().toUpperCase());
        tjChgModeR.setEvent_id(ConfigWago.getJcM()+"-"+Utils.getCurrentDateFormat(ConfigWago.getEventidtimeformat()));
        tjChgModeR.setError_c(errorC);
        tjChgModeR.setError_txt(errorTxt);
        tjChgModeR.setTj_id(tj_id);
        tjChgModeR.setTj_m(tj_m);
        tjChgModeR.setNew_tj_mode_c(currtjmodec);
        tjChgModeR.setTimestamp(new Date());

        return tjChgModeR;
    }

    public Wago generateWago(Wago wago) {
        /*
         * send command via OPCUA
         */
        try{
            wago.setCmdType("TJ_CHG_MODE_R");
            //pubInstructionToWago
            if(curr_tj_mode_c.equalsIgnoreCase("TN")){//TN Mode
                wago.setNewTjMode("TN");
            }else if(curr_tj_mode_c.equalsIgnoreCase("OP")){//OP Mode
                wago.setNewTjMode("OP");
            }else if(curr_tj_mode_c.equalsIgnoreCase("MI")){//OP Mode
                wago.setNewTjMode("MI");
            }else if(curr_tj_mode_c.equalsIgnoreCase("MA")){//OP Mode
                wago.setNewTjMode("MA");
            }else if(curr_tj_mode_c.equalsIgnoreCase("OM")){//OP Mode
                wago.setNewTjMode("OM");
            }else if(curr_tj_mode_c.equalsIgnoreCase("OA")){//OP Mode
                wago.setNewTjMode("OA");
            }else{
                //Wrong Cmd, set error code and return to PSA Solace
                wago.setWagoStatusCode(300);
                logger.debug(String.format("TjChgModeR - generateWago - (messageArrived): CurrTjModeStatus is wrong : %s", curr_tj_mode_c));
            }
        }catch(Exception e){
            wago.setWagoStatusCode(309);
            logger.error("TjChgModeR - generateWago: Unkown error");
        }
        return wago;
    }

//    public Wago generateAckWago(Wago wago) {
//        /*
//         * send command via OPCUA
//         */
//        try{
//            wago.setCmdType("TJ_CHG_MODE_R");
//        }catch(Exception e){
//            wago.setWagoStatusCode(99999);
//            logger.error("TjChgModeR - generateWago: Unkown error");
//        }
//        return wago;
//    }

    public String generateFaultMsg(tj_chg_mode_r tjChgModeR) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + tjChgModeR.getSource_m() +
                "\",\"event_dt\":\"" + tjChgModeR.getTj_chg_mode_r_pk().getEvent_dt() +
                "\",\"event_id\":\"" + tjChgModeR.getEvent_id() +
                "\",\"trans_id\":\"" + tjChgModeR.getTj_chg_mode_r_pk().getTrans_id() +
                "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"error_c\":\"Failed" +
                "\",\"error_txt\":\""+ tjChgModeR.getError_txt() +
                "\",\"tj_id\":" + tjChgModeR.getTj_id() +
                ",\"tj_m\":\"" + tjChgModeR.getTj_m() +
                "\",\"curr_tj_mode_c\":\"" + tjChgModeR.getNew_tj_mode_c() + "\"}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

    public String generateReturnMsg(tj_chg_mode_r tjChgModeR) {
        String sJson = "";
        String standardHeader = "";
        String bodyMsg = "";

        standardHeader = "\"gtosplus_ops_header\":{\"source_m\":\"" + tjChgModeR.getSource_m() +
                "\",\"event_dt\":\"" + tjChgModeR.getTj_chg_mode_r_pk().getEvent_dt() +
                "\",\"event_id\":\"" + tjChgModeR.getEvent_id() +
                "\",\"trans_id\":\"" + tjChgModeR.getTj_chg_mode_r_pk().getTrans_id() +
                "\"}";
        bodyMsg = "\"gtosplus_ops_body\":{\"error_c\":\"" + tjChgModeR.getError_c() +
                "\",\"error_txt\":\"" + tjChgModeR.getError_txt() +
                "\",\"tj_id\":" + tjChgModeR.getTj_id() +
                ",\"tj_m\":\"" + tjChgModeR.getTj_m() +
                "\",\"curr_tj_mode_c\":\"" + tjChgModeR.getNew_tj_mode_c() + "\"}";

        // Construct Json Message
        sJson = String.format("{%s,%s}", standardHeader, bodyMsg);

        return sJson;
    }

}
