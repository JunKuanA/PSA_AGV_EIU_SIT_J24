package module.process;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import module.entities.*;
import module.message.*;
import module.object.OPCUAWagoHM;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.psa.gtosplus.esb.client.ESBClient;
import com.psa.gtosplus.esb.client.exception.ESBClientException;

import module.config.ConfigWago;
import module.utils.Utils;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import static module.Subscribenode.multipleSubscribe.handlerNode;

public class ProcessMsg2 {
    private static Logger logger = Logger.getLogger(ProcessMsg2.class.getName());
    private ESBClient esbClient = null;
    private persistenceManager dbCtrl = null;
    private HashMap<String, Object> hmDbParam = null;

    private Payload payload = null;
    private Gson gson = null;
    private Wago wago = null;

    private jc_upd_st jcUpdStDB = null;
    private JcUpdSt jcUpdStMsg = null;
    private int jcId = 0;
    private String jcM = "";

    private int tjId = 0;
    private String tjM = "";
    private String opsTransId = "";
    private String newJcSt = "";

    private String newTjChgModeR = "";

    private jc_enq_info jcEnqInfoDB = null;
    private JcEnqInfo jcEnqInfoMsg = null;

    private tj_chg_mode_r TjChgModeRDB = null;
    private TjChgModeR TjChgModeRMsg = null;

    private String error_c = "";

    private String error_txt = "";

    private String jcStatusC = "";
    private String mi_i = "";
    private String controlMode = "";
    private String barriers_down_i = "";
    private String traffic_light_i = "";
    private String mnl_xing_req_i = "";
    private int numOfPmLoop = 0;
    private String pmLoopList = null;
    private int numOfUturn = 0;
    private String uturnLoopList = null;
    private int numOfFaults = 0;
    private String faultCodeDescList = null;

    private jc_rst_alrm jcRstAlrmDB = null;
    private JcRstAlrm jcRstAlrmMsg = null;
    private int numOfFault = 0;
    private OpcUaClient opcUaClient = null;

    public ProcessMsg2(ESBClient esbClient, persistenceManager dbCtrl,HashMap<String, Object> hmDbParam,OpcUaClient opcUaClient, Wago wago) {
        this.esbClient = esbClient;
        this.dbCtrl = dbCtrl;
        this.hmDbParam = hmDbParam;
        this.opcUaClient = opcUaClient;
        this.wago = wago;
        if (hmDbParam.size()>0){hmDbParam.clear();}
        wago.setWagoStatusCode(0);
        wago.setWagoStatusCodeMssg("");
    }
    public ProcessMsg2(ESBClient esbClient, persistenceManager dbCtrl,HashMap<String, Object> hmDbParam) {
        this.esbClient = esbClient;
        this.dbCtrl = dbCtrl;
        this.hmDbParam = hmDbParam;
        if (hmDbParam.size()>0){hmDbParam.clear();}
    }

    public void onMessageReceived2(String esbTransId, String payloadX) {
        payload = null;
        gson = new Gson();
        // wago = new Wago();
        // Convert JSON File to Java Object
        try {
            payload = gson.fromJson(payloadX, Payload.class);
            logger.info("message received" + payloadX);
        } catch (Exception e) {
            logger.error(String.format("EIUProcessStep2 - onMessageReceived - payloadX: %s", payloadX), e);
            return;
        }

        jcUpdStDB = null;
        jcUpdStMsg = null;
        jcId = 0;
        jcM = "";
        opsTransId = "";
        newJcSt = "";

        jcEnqInfoDB = null;
        jcEnqInfoMsg = null;
        mi_i = "";
        controlMode = "";
        barriers_down_i = "";
        traffic_light_i = "";
        mnl_xing_req_i = "";
        numOfPmLoop = 0;
        pmLoopList = "";
        numOfUturn = 0;
        uturnLoopList = "";

        jcRstAlrmDB = null;
        jcRstAlrmMsg = null;
        numOfFault = 0;
        faultCodeDescList = "";

        TjChgModeRDB = null;
        TjChgModeRMsg = null;
        error_c = "";
        error_txt = "";
        tjId = 0;
        tjM = "";
        newTjChgModeR = "";


        logger.debug(wago.getWagoStatusCodeMssg());
        logger.debug(wago.getWagoStatusCode());
        verifyTopic(esbTransId);
        logger.debug("topic verified");
        logger.debug(wago.getWagoStatusCode());

        /*
         * Recevie new message for topic
         */
        if (esbTransId.equalsIgnoreCase(ConfigWago.getEsbJcEnqInfo())){
            if (hmDbParam.size()>0){hmDbParam.clear();}
            hmDbParam.put("eventDt", jcEnqInfoDB.getJc_enq_info_pk().getEvent_dt());
            jc_enq_info jcEnqInfoDBResult = (jc_enq_info) dbCtrl.findEntity("jc_enq_info.findByEventDt", hmDbParam);
            if (jcEnqInfoDBResult != null){
                logger.warn("ProcessMsg - onMessageReceived - jcEnqInfo: Duplicate Entry");
                wago.setWagoStatusCode(302);
            }else{
                try{
                    dbCtrl.persistEntity(jcEnqInfoDB);
                }catch (Exception e) {
                wago.setWagoStatusCode(303);
                logger.error("ProcessMsg - onMessageReceived - jcEnqInfo: Fail to insert database", e);
                }
            }
            ///////////////// to obtain JC_ENQ_INFO message ///////////////
            jcEnqInfoMsg = payload.generatePayloadJcEnqInfo();
            if (jcEnqInfoMsg == null || jcEnqInfoMsg.getMsgHeader() == null){
                logger.error("ProcessMsg - onMessageReceived - jcEnqInfo: Invalid Json Input");
                wago.setWagoStatusCode(300);
                return;
            }
            mnl_xing_req_i = jcEnqInfoMsg.getMnl_xing_req_i();

            if (wago.getWagoStatusCode() == 0) {
                wago.setEsbClient(esbClient);
                // callProcessThread(wago);
                try{
                    ExecutorService enqpushinfothread = Executors.newSingleThreadExecutor();
                    Future<Void> enqpushinfothreadFuture = enqpushinfothread.submit(new EnqPushInfoCallable(dbCtrl, esbClient,hmDbParam,opcUaClient,mnl_xing_req_i));
                                        
                    enqpushinfothreadFuture.get();
                    enqpushinfothread.shutdownNow();
                }catch (Exception e){
                    logger.error("callProcessThread - jc_enq_info : " + e.getMessage());
                    // Thread.currentThread().interrupt();
                }
            }else{
                pubFaultMsg(wago);
            }
        } else if (esbTransId.equalsIgnoreCase(ConfigWago.getEsbJcUpdSt())){
            try{
                /*
                 * Receive new message for topic eqptInstr (AAACS instruction to Equipment) on PSA Solace MQTT
                 */
                if (hmDbParam.size()>0){hmDbParam.clear();}
                hmDbParam.put("eventDt", jcUpdStDB.getJc_upd_st_pk().getEvent_dt());
                jc_upd_st jcUpdStDBResult = (jc_upd_st) dbCtrl.findEntity("jc_upd_st.findByEventDt", hmDbParam);
                /*
                 * Check jcupdst table in EIU database, if record exist, publish duplicated entry to PSA solace MQTT
                 * if record not exist, insert record into aaEqptInstr table
                 */
                if (jcUpdStDBResult != null) {
                    logger.warn("ProcessMsg - onMessageReceived - jcUpdSt: Duplicate Entry");
                    wago.setWagoStatusCode(302);
                } else {
                    try {
                        dbCtrl.persistEntity(jcUpdStDB);
                    } catch (Exception e) {
                        logger.error("ProcessMsg - onMessageReceived - jcUpdSt: Fail to insert database", e);
                        wago.setWagoStatusCode(303);
                    }
                }

                /*
                 * Preparation before executing instruction
                 */
                checkInfo();
                logger.debug("info verified");
                if (wago.getWagoStatusCode() == 0) {
                    wago.setEsbClient(esbClient);
                    List<equipment_info> equipmentInfo = persistenceManager.getAllHB();
                    int heartbeatRunningNumber = equipmentInfo.get(2).getLast_hb();
                    try {
                        heartbeatRunningNumber = (heartbeatRunningNumber + 1) % 10000;
                        try {
                            persistenceManager.updateJcHB(heartbeatRunningNumber);
                            logger.debug("updatehb");
                        } catch (Exception e) {
                            logger.error("JC publish hb - rub : ", e);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        logger.error("Jc publish hb - run", e);
                        Thread.currentThread().interrupt();
                    }
                    
                    // callProcessThread(wago);
                    try{
                        ExecutorService jcUpdStthread = Executors.newSingleThreadExecutor();
                        Future<Void> jcUpdStthreadFuture = jcUpdStthread.submit(new JcUpdStCallable(dbCtrl, esbClient,hmDbParam,opcUaClient,wago));
                        logger.debug("jc status cmd sent");
                        jcUpdStthreadFuture.get();
                        jcUpdStthread.shutdownNow();
                        logger.debug("upd_st thread shutdown");
                    }catch (Exception e){
                        logger.error("callProcessThread - jc_upd_st: " + e.getMessage());
                        // Thread.currentThread().interrupt();
                    }
                }else{
                    pubFaultMsg(wago);
                }
            }catch(Exception e){
                wago.setWagoStatusCode(309); // Unknown Error
                logger.error("ProcessMsg - onMessageReceived - jcUpdSt: Unknown Error", e);
            }
        }else if (esbTransId.equalsIgnoreCase(ConfigWago.getEsbTjChgModeR())){
            if (hmDbParam.size()>0){hmDbParam.clear();}
            hmDbParam.put("eventDt", TjChgModeRDB.getTj_chg_mode_r_pk().getEvent_dt());
            tj_chg_mode_r TjChgModeRDBResult = (tj_chg_mode_r) dbCtrl.findEntity("tj_chg_mode_r.findByEventDt", hmDbParam);
            if (TjChgModeRDBResult != null){
                logger.warn("ProcessMsg - onMessageReceived - TjChgModeR: Duplicate Entry");
                wago.setWagoStatusCode(302);
            }else{
                try{
                    dbCtrl.persistEntity(TjChgModeRDB);
                }catch (Exception e) {
                    wago.setWagoStatusCode(303);
                    logger.error("ProcessMsg - onMessageReceived - TjChgModeR: Fail to insert database", e);
                }
            }
            if (wago.getWagoStatusCode() == 0) {
                wago.setEsbClient(esbClient);
                // callProcessThread(wago);
                try{
                    ExecutorService tjchgmoderthread = Executors.newSingleThreadExecutor();
                    Future<Void> tjchgmoderthreadFuture = tjchgmoderthread.submit(new TjChgModeRCallable(dbCtrl, esbClient,hmDbParam,opcUaClient,wago));

                    tjchgmoderthreadFuture.get();
                    tjchgmoderthread.shutdownNow();
                }catch (Exception e){
                    logger.error("callProcessThread - Tj_Chg_Mode_R : " + e.getMessage());
                    // Thread.currentThread().interrupt();
                }
            }else{
                pubFaultMsg(wago);
            }
        }
    }

    private void verifyTopic(String esbTransId) {
        /*
         * Verify Topic and device available
         */
        if (esbTransId.equalsIgnoreCase(ConfigWago.getEsbJcUpdSt())){
            // logger.debug("jc_upd_st received");
            jcUpdStMsg = payload.generatePayloadJcUpdSt();
            if (jcUpdStMsg == null || jcUpdStMsg.getMsgHeader() == null){
                wago.setWagoStatusCode(300);
                logger.error("ProcessMsg - onMessageReceived - jcUpsSt: Invalid Json Input");
                return;
            }
            jcId = jcUpdStMsg.getJc_id();
            jcM = jcUpdStMsg.getJc_m();
            opsTransId = jcUpdStMsg.getMsgHeader().getTransId();
            jcUpdStDB = jcUpdStMsg.generateJcUpdSt();
            newJcSt = jcUpdStDB.getNew_jc_status_c();
            wago = jcUpdStMsg.generateWago(wago);
            wago.setJcSt(jcUpdStMsg);

            if (!MessageValidation.isJsonRequestMessageValid(jcUpdStMsg)) {
                wago.setWagoStatusCode(301);
                logger.error("ProcessMsg - onMessageReceived - jcUpdSt: Invalid Message Format");
                return;
            }
            try{
                wago.setErrorC("OK");
                ExecutorService jcAckRthread = Executors.newSingleThreadExecutor();
                Future<Void> jcAckRthreadFuture = jcAckRthread.submit(new JcAckRCallable(dbCtrl, esbClient,hmDbParam,opcUaClient,wago));
                            
                jcAckRthreadFuture.get();
                jcAckRthread.shutdownNow();
                logger.debug("ack r thread shutdown");
            }catch (Exception e){
                logger.error("CheckInfo - JC_ACK_R : " + e.getMessage());
                // Thread.currentThread().interrupt();
            }
        } else if (esbTransId.equalsIgnoreCase(ConfigWago.getEsbJcEnqInfo())){
            jcEnqInfoMsg = payload.generatePayloadJcEnqInfo();
            if (jcEnqInfoMsg == null || jcEnqInfoMsg.getMsgHeader() == null){
                logger.error("ProcessMsg - onMessageReceived - jcEnqInfo: Invalid Json Input");
                wago.setWagoStatusCode(300);
                return;
            }
            jcId = jcEnqInfoMsg.getJc_id();
            jcM = jcEnqInfoMsg.getJc_m();
            mnl_xing_req_i = jcEnqInfoMsg.getMnl_xing_req_i();
            opsTransId = jcEnqInfoMsg.getMsgHeader().getTransId();
            jcEnqInfoDB = jcEnqInfoMsg.generateJcEnqInfo();
            wago = jcEnqInfoMsg.generateWago(wago);
            wago.setJcEnqInfo(jcEnqInfoMsg);

            if (!MessageValidation.isJsonRequestMessageValid(jcEnqInfoMsg)) {
                wago.setWagoStatusCode(301);
                logger.error("ProcessMsg - onMessageReceived - jcEnqInfo: Invalid Message Format");
                return;
            }
        }else if (esbTransId.equalsIgnoreCase(ConfigWago.getEsbTjChgModeR())){
            TjChgModeRMsg = payload.generatePayloadTjChgModeR();
            if (TjChgModeRMsg == null || TjChgModeRMsg.getMsgHeader() == null){
                logger.error("ProcessMsg - onMessageReceived - TjChgModeR: Invalid Json Input");
                wago.setWagoStatusCode(300);
                return;
            }

            tjId = TjChgModeRMsg.getTj_id();
            tjM = TjChgModeRMsg.getTj_m();
            opsTransId = TjChgModeRMsg.getMsgHeader().getTransId();
            TjChgModeRDB = TjChgModeRMsg.generateTjChgModeR();
            newTjChgModeR = TjChgModeRDB.getNew_tj_mode_c();
            wago = TjChgModeRMsg.generateWago(wago);
            wago.setTjChgModeR(TjChgModeRMsg);

            if (!MessageValidation.isJsonRequestMessageValid(TjChgModeRMsg)) {
                wago.setWagoStatusCode(301);
                logger.error("ProcessMsg - onMessageReceived - TjChgModeRMsg: Invalid Message Format");
                return;
            }
        }
    }

    private void checkInfo() {
        /*
         * Check junction information before executing instruction
         */
        handlerNode(opcUaClient, "acienqtags.properties");
        for (String s : OPCUAWagoHM.OPCUAHm.keySet()) { 
        if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL")) {
            if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                wago.setCurJcSt("PM_Red");
            } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                wago.setCurJcSt("PM_Green");
            }
        }
    }
        if (hmDbParam.size()>0){hmDbParam.clear();}
        hmDbParam.put("eqpt_name", String.valueOf("plc"));
        equipment_info equipmentInfoDBResult = (equipment_info) dbCtrl.findEntity("equipment_info.findByEqptName", hmDbParam);
        logger.debug(newJcSt);
        logger.debug(wago.getCurJcSt());
        if (wago.getWagoStatusCode() != 0) {
            // Skip other checks
            return;
        } else if (!opsTransId.equalsIgnoreCase(ConfigWago.getEsbJcUpdSt()) && !opsTransId.equalsIgnoreCase(ConfigWago.getEsbJcEnqInfo())) {
            logger.error("onMessageReceived - Invalid trans_id");
            wago.setWagoStatusCode(304); // Invalid Transaction ID
        } else if (!jcM.equalsIgnoreCase(ConfigWago.getJcM())) {
            logger.error(String.format("checkInfo - jc_name: %s not in aciconfig", jcM));
            wago.setWagoStatusCode(305); // Invalid Junction name
        } else if (!ConfigWago.getDeviceList().contains(String.valueOf(jcId))) {
            logger.error(String.format("checkInfo - jc_id: %s not in aciconfig", jcId));
            wago.setWagoStatusCode(306); //Invalid junction id
        } else if (equipmentInfoDBResult == null) {
            wago.setWagoStatusCode(300); // Invalid PLC EQPT name
        } else if (!Utils.checkDateWithInTolerance(equipmentInfoDBResult.getLast_comm(), ConfigWago.getInfoTolerance())) {
            logger.error("onMessageReceived - PLC Communication Lost since: "+equipmentInfoDBResult.getLast_comm());
            wago.setWagoStatusCode(307); // PLC communication lost
        } else if (wago.getControlMode().equalsIgnoreCase("Local")) {
            logger.error(String.format("checkInfo - junction in %s mode cannot receive command", wago.getControlMode()));
            wago.setWagoStatusCode(313); // Invalid Request Type
        } else if (!newJcSt.equalsIgnoreCase("PM_RED") && !newJcSt.equalsIgnoreCase("PM_GREEN")) {
            logger.error(String.format("checkInfo - invalid request: %s", newJcSt));
            wago.setWagoStatusCode(308); // Invalid Request Type
        // }else if(newJcSt.equalsIgnoreCase(wago.getCurJcSt())){
        //     logger.error(String.format("checkInfo - junction already in: %s", newJcSt));
        //     wago.setWagoStatusCode(312); // Invalid Request Type
        }

    }

    private void pubFaultMsg(Wago wago) {
        String sJsonFaultMsg = "";
        String sReplyTransID = "";

        if (hmDbParam.size()>0){hmDbParam.clear();}
        hmDbParam.put("statusCode", wago.getWagoStatusCode());
        try{
            status_code_list statusCodeDBResult = (status_code_list) dbCtrl.findEntity("status_code_list.findByStatusCode", hmDbParam);
            if (statusCodeDBResult != null){
                //wago.setWagoStatusCodeMssg(statusCodeDBResult.getStatus_code_mssg());
                // wago.setWagoStatusDesc(statusCodeDBResult.getStatus_desc());
                wago.setWagoStatusCodeMssg(statusCodeDBResult.getStatus_desc());
            } else {
                logger.error(String.format("ProcessMsg - pubFaultMsg : Fault Code %s Not Found", wago.getWagoStatusCode()));
                wago.setWagoStatusCodeMssg("Fault Code Not Found");
                // wago.setWagoStatusDesc("Fault Code Not Found");
                wago.setWagoStatusCode(310);
            }
        }catch(Exception e){
            logger.error("ProcessMsg - pubFaultMsg : " + e.getMessage());
        }
        try{
            if (wago.getCmdType().equalsIgnoreCase("JC_UPD_ST")){
                wago.setErrorC("Failed");
                try{
            
                    ExecutorService jcAckRthread = Executors.newSingleThreadExecutor();
                    Future<Void> jcAckRthreadFuture = jcAckRthread.submit(new JcAckRCallable(dbCtrl, esbClient,hmDbParam,opcUaClient,wago));
                                
                    jcAckRthreadFuture.get();
                    jcAckRthread.shutdownNow();
                }catch (Exception e){
                    logger.error("CheckInfo - JC_ACK_R : " + e.getMessage());
                    // Thread.currentThread().interrupt();
                }
                logger.debug("JC cur status: "+ wago.getCurJcSt());
                jc_upd_st_r jcUpdStRDb = wago.getJcSt().generateJcUpdStR("Failed",wago.getWagoStatusCodeMssg(),wago.getCurJcSt());
                sJsonFaultMsg = wago.getJcSt().generateFaultMsg(jcUpdStRDb);
                sReplyTransID = ConfigWago.getEsbJcUpdStR().toUpperCase();
                logger.debug("JC_UPD_ST_R fault message: \n" + sJsonFaultMsg);
                try {
                    dbCtrl.persistEntity(jcUpdStRDb);
                } catch (Exception e) {
                    logger.error("ProcessMsg - pubFaultMsg : JC_UPD_ST_R Data Insert DB Failed", e);
                    wago.setWagoStatusCode(305);
                }
            } else if (wago.getCmdType().equalsIgnoreCase("JC_ENQ_INFO")){
                jc_enq_info_r jcEnqInfoRDb = wago.getJcEnqInfo().generateJcEnqInfoR("Failed",wago.getWagoStatusCodeMssg());
                sJsonFaultMsg = wago.getJcEnqInfo().generateFaultMsg(jcEnqInfoRDb);
                sReplyTransID = ConfigWago.getEsbJcEnqInfoR().toUpperCase();
                logger.debug("JC_ENQ_INFO_R fault message: \n" + sJsonFaultMsg);
                try {
                    dbCtrl.persistEntity(jcEnqInfoRDb);
                } catch (Exception e) {
                    logger.error("ProcessMsg - pubFaultMsg : JC_ENQ_INFO_R Data Insert DB Failed", e);
                    wago.setWagoStatusCode(303);
                }
            }else if (wago.getCmdType().equalsIgnoreCase("TJ_CHG_MODE_R")){
                tj_chg_mode_r tjChgModeRDb = wago.getTjChgModeR().generateTjChgModeR();
                sJsonFaultMsg = wago.getTjChgModeR().generateFaultMsg(tjChgModeRDb);
                sReplyTransID = ConfigWago.getEsbTjChgModeR().toUpperCase();
                logger.debug("TJ_CHG_MODE_R fault message: \n" + sJsonFaultMsg);
                try {
                    dbCtrl.persistEntity(tjChgModeRDb);
                } catch (Exception e) {
                    logger.error("ProcessMsg - pubFaultMsg : TJ_CHG_MODE_R Data Insert DB Failed", e);
                    wago.setWagoStatusCode(303);
                }
            }
        }catch (Exception e) {
            logger.error("ProcessMsg - pubFaultMsg : ",e);
        } finally{
            publishMessage(sReplyTransID, sJsonFaultMsg);
        }

    }

    public void publishMessage(String transId, String message) {
        try {
            esbClient.tryPublishMessage(transId, message);
        } catch (ESBClientException e) {
            logger.error("ProcessMsg - publishMessage : " + e.getMessage());
        }
    }
}
