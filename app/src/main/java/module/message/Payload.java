package module.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Payload {
    private Gson gson = new Gson();
    @SerializedName("gtosplus_ops_header")
    private JsonObject gtosplusOpsHeader;
    @SerializedName("gtosplus_ops_body")
    private JsonObject gtosplusOpsBody; 

    public JsonObject getGtosplusOpsHeader() {
        return gtosplusOpsHeader;
    }

    public void setGtosplusOpsHeader(JsonObject gtosplusOpsHeader) {
        this.gtosplusOpsHeader = gtosplusOpsHeader;
    }

    public JsonObject getGtosplusOpsBody() {
        return gtosplusOpsBody;
    }

    public void setGtosplusOpsBody(JsonObject gtosplusOpsBody) {
        this.gtosplusOpsBody = gtosplusOpsBody;
    }

    public MsgHeader generatePayloadHeader(){
        MsgHeader msgHeader = null;
        try{
            msgHeader = gson.fromJson(gtosplusOpsHeader, MsgHeader.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return msgHeader;
    }
    
    public JcUpdSt generatePayloadJcUpdSt(){
        JcUpdSt jcUpdSt = null;
        try{
            jcUpdSt = gson.fromJson(gtosplusOpsBody, JcUpdSt.class);
            jcUpdSt.setMsgHeader(generatePayloadHeader());
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return jcUpdSt;
    }

    public JcAckR generatePayloadJcAckR(){
        JcAckR jcAckR = null;
        try{
            jcAckR = gson.fromJson(gtosplusOpsBody, JcAckR.class);
            jcAckR.setMsgHeader(generatePayloadHeader());
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return jcAckR;
    }

    public JcRstAlrm generatePayloadJcRstAlrm(){
        JcRstAlrm jcRstAlrm = null;
        try{
            jcRstAlrm = gson.fromJson(gtosplusOpsBody, JcRstAlrm.class);
            jcRstAlrm.setMsgHeader(generatePayloadHeader());
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return jcRstAlrm;
    }

    public JcEnqInfo generatePayloadJcEnqInfo() {
        JcEnqInfo jcEnqInfo = null;
        try{
            jcEnqInfo = gson.fromJson(gtosplusOpsBody, JcEnqInfo.class);
            jcEnqInfo.setMsgHeader(generatePayloadHeader());
        }catch(Exception e){
            e.printStackTrace();
        }
        return jcEnqInfo;
    }

    public TjChgModeR generatePayloadTjChgModeR() {
        TjChgModeR tjChgModeR = null;
        try{
            tjChgModeR = gson.fromJson(gtosplusOpsBody, TjChgModeR.class);
            tjChgModeR.setMsgHeader(generatePayloadHeader());
        }catch(Exception e){
            e.printStackTrace();
        }
        return tjChgModeR;
    }

    public JcUturnDtecInfo generatePayloadJcUturnDtecInfo() {
        JcUturnDtecInfo jcUturnDtecInfo = null;
        try{
            jcUturnDtecInfo = gson.fromJson(gtosplusOpsBody, JcUturnDtecInfo.class);
            jcUturnDtecInfo.setMsgHeader(generatePayloadHeader());
        }catch(Exception e){
            e.printStackTrace();
        }
        return jcUturnDtecInfo;
    }

    public JcPmDtecInfo generatePayloadJcPmDtecInfo() {
        JcPmDtecInfo jcPmDtecInfo = null;
        try{
            jcPmDtecInfo = gson.fromJson(gtosplusOpsBody, JcPmDtecInfo.class);
            jcPmDtecInfo.setMsgHeader(generatePayloadHeader());
        }catch(Exception e){
            e.printStackTrace();
        }
        return jcPmDtecInfo;
    }

    public JcHrtbtInfo generatePayloadJcHrtbtInfo() {
        JcHrtbtInfo jcHrtbtInfo = null;
        try{
            jcHrtbtInfo = gson.fromJson(gtosplusOpsBody, JcHrtbtInfo.class);
            jcHrtbtInfo.setMsgHeader(generatePayloadHeader());
        }catch(Exception e){
            e.printStackTrace();
        }
        return jcHrtbtInfo;
    }

    public JcAlrmInfo generatePayloadJcAlrmInfo() {
        JcAlrmInfo jcAlrmInfo = null;
        try{
            jcAlrmInfo = gson.fromJson(gtosplusOpsBody, JcAlrmInfo.class);
            jcAlrmInfo.setMsgHeader(generatePayloadHeader());
        }catch(Exception e){
            e.printStackTrace();
        }
        return jcAlrmInfo;
    }
    
}
