package module.process;

import com.google.gson.Gson;
import com.psa.gtosplus.esb.client.ESBClient;
import com.psa.gtosplus.esb.client.exception.ESBClientException;

import module.Writenode.writeNode1;
import module.config.ConfigWago;
import module.entities.jc_ack_r;
import module.entities.jc_alrm_info;
import module.entities.jc_enq_info_r;
import module.entities.jc_upd_st_r;
import module.entities.persistenceManager;
import module.entities.status_code_list;
import module.message.JcAckR;
import module.message.JcEnqInfo;
import module.message.Payload;
import module.object.OPCUAWagoHM;
import module.utils.Utils;
import module.message.JcAlrmInfo;

import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import com.google.gson.Gson;
import com.psa.gtosplus.esb.client.ESBClient;
import com.psa.gtosplus.esb.client.exception.ESBClientException;
import module.config.ConfigWago;
import module.entities.equipment_info;
import module.entities.jc_alrm_info;
import module.entities.persistenceManager;
import module.message.JcAlrmInfo;
import module.message.Payload;
import module.object.OPCUAWagoHM;
import module.utils.Utils;

import static module.Subscribenode.multipleSubscribe.handlerNode;

import static module.Subscribenode.multipleSubscribe.handlerNode;

public class JcUpdStCallable implements Callable<Void> {
    private static Logger logger = Logger.getLogger(JcUpdStCallable.class.getName());
    private module.entities.persistenceManager persistenceManager = null;
    private ESBClient esbClient = null;
    private Wago wago = null;
    private String sJsonReturnMsg = "";
    private String sReplyTransID = "";
    private Payload payload = null;
    private Gson gson = null;
    private OpcUaClient opcUaClient = null;
    private HashMap<String, Object> hmDbParam = null;
    private String error_c = "";
    private String error_txt = "";
    private String prev_trans_id = "";

    private Thread processMsgToWagoThread = null;
    private int threadId = 0;
    private String threadName = "";
    private boolean processing = true;
    private boolean alarm = false;
    private int num_of_faults = 0;
    private List<String> fault_desc_list = null;
    private boolean taskIncomplete = false;
    private boolean miOccured = true;
    private boolean dgrdmode = false;
    private boolean mamode =false;
    private boolean plcdown =false;
    private boolean plcerrorlock =true;
    private JcAlrmInfo jcAlrmInfoM = null;

    
    private Thread alarmThread = null;
    public static boolean PLCAlive = true;
    private persistenceManager dbCtrl = null;

    public JcUpdStCallable(persistenceManager persistenceManager, ESBClient esbClient,
            HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient, Wago wago) {
        this.persistenceManager = persistenceManager;
        this.esbClient = wago.getEsbClient();
        this.wago = wago;
        this.hmDbParam = hmDbParam;
        this.opcUaClient = opcUaClient;
        if (hmDbParam.size() > 0) {
            hmDbParam.clear();
        }
    }

    public JcUpdStCallable(module.entities.persistenceManager persistenceManager, OpcUaClient opcUaClient,
            HashMap<String, Object> hmDbParam, Wago wago) {
        this.persistenceManager = persistenceManager;
        this.wago = wago;
        this.hmDbParam = hmDbParam;
        this.opcUaClient = opcUaClient;
        if (hmDbParam.size() > 0) {
            hmDbParam.clear();
        }
    }

    @Override
    public Void call() throws Exception {
        this.processing = true;
        Boolean flagError = false;
        Boolean miflag = false;
        payload = null;
        sJsonReturnMsg = "";
        sReplyTransID = "";
        String curr_status = "";

        if ("PM_Red".equalsIgnoreCase(wago.getNewJcSt())) {
            try {
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL", true);
                curr_status = "PM_RED";
            } catch (Exception e) {
                logger.error("call : OPCUA write node failed:", e);
            }
        } else {
            try {
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL", false);
                curr_status = "PM_GREEN";
            } catch (Exception e) {
                logger.error("call : OPCUA write node failed:", e);
            }
        }
        logger.debug("alarmCallable started - monitoring fault");
        while (processing) {
            try {
                logger.info(wago.getPlcLost());
                List<Date> equipmentInfo = persistenceManager.getJcLastComm();
                handlerNode(opcUaClient, "acienqtags.properties");
                for (String s : OPCUAWagoHM.OPCUAHm.keySet()) {
                    // logger.info("Outside timeout: check JC timeout:" + Utils.checkDateWithInTolerance(equipmentInfo.get(0), ConfigWago.getThreadTimeout()));
                    // logger.info(String.format("Inside: JC-ALRM-INFO - Junction clr = " + OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Junction_Clr")));
                    // logger.info(String.format("Inside: JC-ALRM-INFO - TL_PH_A = " + OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_A")));
                    // logger.info(String.format("Inside: JC-ALRM-INFO - TL_PH_B = " + OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_B")));
                    // logger.info(String.format("Inside: JC-ALRM-INFO - TL_PH_C = " + OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_C")));

                    if ("PM_Red".equalsIgnoreCase(wago.getNewJcSt()) && s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Junction_Clr") && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur")=="false") {
                        logger.debug(s + " " + OPCUAWagoHM.OPCUAHm.get(s));
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            logger.debug("call - junction clear");
                            wago.setNewJcSt(wago.getNewJcSt());
                            processing = false;
                            // break;
                        }
                    }else if ("PM_Green".equalsIgnoreCase(wago.getNewJcSt()) && (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_A") || s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_B") || s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_C")) && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur")=="false") {
                        logger.debug(s + " " + OPCUAWagoHM.OPCUAHm.get(s));
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            logger.debug("call - junction clear");
                            wago.setNewJcSt(wago.getNewJcSt());
                            processing = false;
                            // break;
                        }
                    }else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "true") {
                        logger.debug(s + " " + OPCUAWagoHM.OPCUAHm.get(s));
                        logger.debug("maintenance mode");
                        // wago.setNewJcSt(wago.getNewJcSt());
                        processing = false;
                        // flagError = true;
                        // break;
                    }else if(!Utils.checkDateWithInTolerance(equipmentInfo.get(0), ConfigWago.getThreadTimeout()) && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur")=="false"){
                        logger.info("Inside timeout: check JC timeout:" + Utils.checkDateWithInTolerance(equipmentInfo.get(0), ConfigWago.getThreadTimeout()));
                        logger.debug("jc last send:"+equipmentInfo.get(0));
                        logger.debug("timeout occur");
                        processing=false;
                        flagError=true;
                    // }else if(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur")=="true"){
                    //     List<equipment_info> equipment = persistenceManager.getAllHB();
                    //     int heartbeatRunningNumber = equipment.get(2).getLast_hb();
                    //     try {
                    //         heartbeatRunningNumber = (heartbeatRunningNumber + 1) % 10000;
                    //         try {
                    //             persistenceManager.updateJcHB(heartbeatRunningNumber);
                    //             logger.debug("updatehb");
                    //         } catch (Exception e) {
                    //             logger.error("JC publish hb - rub : ", e);
                    //         }
                    //     } catch (Exception e) {
                    //         // TODO Auto-generated catch block
                    //         logger.error("Jc publish hb - run", e);
                    //         Thread.currentThread().interrupt();
                    //     }
                    }else if(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur")=="true"){
                        logger.debug("mi occur");
                        processing=false;
                        miflag=true;
                    // }else if("true".equalsIgnoreCase(wago.getPlcLost())) {
                    //     logger.debug("call - PLC Lost Comm");
                    //     processing=false;
                    //     flagError=true;
                    //     // wago.setPlcLost("true");
                    }
                    
                    // InetAddress geek = InetAddress.getByName("10.106.160.1");
                // System.out.println("Sending Ping Request to " + ipAddress);
                // boolean isReachable = geek.isReachable(10000);
                // if(!isReachable){
                // // logger.info("10.106.160.1" + " is " + (isReachable ? "reachable" : "not reachable"));
                // wago.setPlcLost("true");
                // }else{
                //     wago.setPlcLost("false");
                //     logger.info("plc reachable");
                // }
                }
                if("true".equalsIgnoreCase(wago.getPlcLost())) {
                    logger.debug("call - PLC Lost Comm");
                    processing=false;
                    flagError=true;
                    // wago.setPlcLost("true");
                }
            } catch (Exception e) {
                logger.error("Jc Upd call - OPCUA listener : OPCUA write node failed:", e);
            }
            // break;
            // Thread.sleep(10 * 1000L);
        }
        
        // List<equipment_info> equipmentInfo = persistenceManager.getAllHB();
        //             int heartbeatRunningNumber = equipmentInfo.get(2).getLast_hb();
        //             try {
        //                 heartbeatRunningNumber = (heartbeatRunningNumber + 1) % 10000;
        //                 try {
        //                     persistenceManager.updateJcHB(heartbeatRunningNumber);
        //                     logger.debug("updatehb");
        //                 } catch (Exception e) {
        //                     logger.error("JC publish hb - rub : ", e);
        //                 }
        //             } catch (Exception e) {
        //                 // TODO Auto-generated catch block
        //                 logger.error("Jc publish hb - run", e);
        //                 Thread.currentThread().interrupt();
        //             }

        // while(alarm){
        //     try {
        //         List<Date> equipmentjc = persistenceManager.getJcLastComm();
        //         handlerNode(opcUaClient, "acienqtags.properties");
        //         for (String s : OPCUAWagoHM.OPCUAHm.keySet()) {
        //             if ("PM_Red".equalsIgnoreCase(wago.getNewJcSt()) && s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_C") && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur")=="false") {
        //                 logger.debug(s + " " + OPCUAWagoHM.OPCUAHm.get(s));
        //                 if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
        //                     logger.debug("call - junction clear");
        //                     wago.setNewJcSt(wago.getNewJcSt());
        //                     alarm = false;
        //                     // break;
        //                 }
        //             }else if ("PM_Green".equalsIgnoreCase(wago.getNewJcSt()) && s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_A") && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur")=="false") {
        //                 logger.debug(s + " " + OPCUAWagoHM.OPCUAHm.get(s));
        //                 if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
        //                     logger.debug("call - junction clear");
        //                     wago.setNewJcSt(wago.getNewJcSt());
        //                     alarm = false;
        //                     // break;
        //                 }
        //             }else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "true") {
        //                 logger.debug(s + " " + OPCUAWagoHM.OPCUAHm.get(s));
        //                 logger.debug("call - MI occur");
        //                 // wago.setNewJcSt(wago.getNewJcSt());
        //                 alarm = false;
        //                 // flagError = true;
        //                 // break;
        //             }else if(!Utils.checkDateWithInTolerance(equipmentjc.get(0), ConfigWago.getThreadTimeout()) && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur")=="false"){
        //                 logger.debug("jc last send:"+equipmentInfo.get(0));
        //                 logger.debug("timeout occur");
        //                 alarm=false;
        //                 flagError=true;
        //             }else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur") == "false"){
        //                 logger.debug("mioccur-false");
        //                 try{
        //                     ExecutorService alarmpushinfothread = Executors.newSingleThreadExecutor();
        //                     Future<Void> alarmpushinfothreadFuture = alarmpushinfothread.submit(new AlarmPubThreadCallable(persistenceManager, esbClient,hmDbParam,opcUaClient, wago));
                                                
        //                     alarmpushinfothreadFuture.get();
        //                     alarmpushinfothread.shutdownNow();
        //                 }catch (Exception e){
        //                     logger.error("callProcessThread - jc_enq_info : " + e.getMessage());
        //                     // Thread.currentThread().interrupt();
        //                 }
        //             }
        //         }
        //     } catch (Exception e) {
        //         logger.error("Jc Upd call - OPCUA listener : OPCUA write node failed:", e);
        //     }
        //     // break;
        // }
        wago.setWagoStatusCodeMssg("");
        wago.setErrorC("");
        sJsonReturnMsg = "";
        sReplyTransID = "";
        try {
            // handlerNode(opcUaClient, "acienqtags.properties");
            if (hmDbParam.size() > 0) {
                hmDbParam.clear();
            }
            hmDbParam.put("faultCode", wago.getWagoStatusCode());
            logger.debug(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE"));
            if (flagError) {
                try {
                    wago.setErrorC("Failed");
                    status_code_list statusCodeDBResult = (status_code_list) persistenceManager
                            .findEntity("status_code_list.findByStatusCode", hmDbParam);
                    if (statusCodeDBResult != null) {
                        // wago.setWagoStatusCodeMssg("Rejected: " +
                        // statusCodeDBResult.getStatus_code_mssg());
                        wago.setWagoStatusCodeMssg(statusCodeDBResult.getStatus_desc());
                    } else {
                        logger.info(
                                String.format("call - findEntity - Fault Code %s Not Found", wago.getWagoStatusCode()));
                        wago.setWagoStatusCodeMssg("Junction time out");
                    }
                } catch (Exception e) {
                    logger.error("call - findEntity", e);
                }
            }else if (miflag) {
                try {
                    wago.setErrorC("Failed");
                    status_code_list statusCodeDBResult = (status_code_list) persistenceManager
                            .findEntity("status_code_list.findByStatusCode", hmDbParam);
                    if (statusCodeDBResult != null) {
                        // wago.setWagoStatusCodeMssg("Rejected: " +
                        // statusCodeDBResult.getStatus_code_mssg());
                        wago.setWagoStatusCodeMssg(statusCodeDBResult.getStatus_desc());
                    } else {
                        logger.info(
                                String.format("call - findEntity - Fault Code %s Not Found", wago.getWagoStatusCode()));
                        wago.setWagoStatusCodeMssg("MI Occured");
                    }
                } catch (Exception e) {
                    logger.error("call - findEntity", e);
                }
            }else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "true" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "false"){
                wago.setWagoStatusCodeMssg("");
                wago.setErrorC("Warning");
            }else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "false" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "false"){
                wago.setWagoStatusCodeMssg("");
                wago.setErrorC("OK");
            }else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "true" && !flagError){
                wago.setWagoStatusCodeMssg("Maintenance Mode Occurred");
                wago.setErrorC("Failed");
            }

            // if("Failed".equalsIgnoreCase(wago.getErrorC())){
            //     for (String s : OPCUAWagoHM.OPCUAHm.keySet()) { 
            //         if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL")) {
            //             if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
            //             wago.setNewJcSt("PM_Green");
            //         } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
            //             wago.setNewJcSt("PM_Red");
            //         }
            //         }
            //     }
            // }

            if("Failed".equalsIgnoreCase(wago.getErrorC())){
                if(curr_status == "PM_RED"){
                    wago.setNewJcSt("PM_Green");
                }else{
                    wago.setNewJcSt("PM_Red");
                }
            }

            jc_upd_st_r jcUpdStRDB = wago.getJcSt().generateJcUpdStR(wago.getErrorC(), wago.getWagoStatusCodeMssg(),
                    wago.getNewJcSt());

            try {
                persistenceManager.persistEntity(jcUpdStRDB);
            } catch (Exception e) {
                logger.error("call - persist entity : JC_UPD_ST_R Data Insert DB Failed", e);
                wago.setWagoStatusCode(303);
            }
            sJsonReturnMsg = wago.getJcSt().generateReturnMsg(jcUpdStRDB);
            sReplyTransID = ConfigWago.getEsbJcUpdStR().toUpperCase();
            logger.debug("JC_UPD_ST_R pub message: \n" + sJsonReturnMsg);
            // Publish out to PSA sollace
            try {
                publishMessage(sReplyTransID, sJsonReturnMsg);
            } catch (Exception e) {
                logger.error("call - publishMessage : ", e);
            }
            
        } catch (Exception e2) {
            logger.error("call : " + e2.getMessage());
            // Thread.currentThread().interrupt();
        }
        return null;
    }

    public void publishMessage(String transId, String message) {
        try {
            esbClient.tryPublishMessage(transId, message);
        } catch (ESBClientException e) {
            logger.error("Junction Upd St- publishMessage Error : " + e.getMessage(), e);
        }
    }

}
