package module.process;

import com.google.gson.Gson;
import com.psa.gtosplus.esb.client.ESBClient;
import com.psa.gtosplus.esb.client.exception.ESBClientException;

import module.Writenode.writeNode1;
import module.config.ConfigWago;
import module.entities.*;
import module.message.JcAckR;
import module.message.JcEnqInfo;
import module.message.Payload;
import module.object.OPCUAWagoHM;
import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static module.Subscribenode.multipleSubscribe.handlerNode;

public class TjChgModeRCallable implements Callable<Void> {
    private static Logger logger = Logger.getLogger(TjChgModeRCallable.class.getName());
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

    public TjChgModeRCallable(persistenceManager persistenceManager, ESBClient esbClient,
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

    public TjChgModeRCallable(module.entities.persistenceManager persistenceManager, OpcUaClient opcUaClient,
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
        payload = null;
        sJsonReturnMsg = "";
        sReplyTransID = "";
        String curr_tj_mode_c = "";

        if ("TN".equalsIgnoreCase(wago.getNewTjMode())) {
            try {
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_TN_Mode", true);
                curr_tj_mode_c = "TN";
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OP_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MI_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OM_Mode", false);
            } catch (Exception e) {
                logger.error("call : OPCUA write node failed:", e);
            }
        } else if ("OP".equalsIgnoreCase(wago.getNewTjMode())){
            try {
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OP_Mode", true);
                curr_tj_mode_c = "OP";
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_TN_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MI_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OM_Mode", false);
            } catch (Exception e) {
                logger.error("call : OPCUA write node failed:", e);
            }
        }else if ("MI".equalsIgnoreCase(wago.getNewTjMode())){
            try {
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MI_Mode", true);
                curr_tj_mode_c = "MI";
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OP_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_TN_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OM_Mode", false);
            } catch (Exception e) {
                logger.error("call : OPCUA write node failed:", e);
            }
        }else if ("MA".equalsIgnoreCase(wago.getNewTjMode())){
            try {
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MA_Mode", true);
                curr_tj_mode_c = "MA";
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OP_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MI_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_TN_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OM_Mode", false);
            } catch (Exception e) {
                logger.error("call : OPCUA write node failed:", e);
            }
        }else if ("OM".equalsIgnoreCase(wago.getNewTjMode())){
            try {
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OM_Mode", true);
                curr_tj_mode_c = "OM";
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OP_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MI_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_TN_Mode", false);
            } catch (Exception e) {
                logger.error("call : OPCUA write node failed:", e);
            }
        }else if ("OA".equalsIgnoreCase(wago.getNewTjMode())){
            try {
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OA_Mode", true);
                curr_tj_mode_c = "OA";
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OP_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MI_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_MA_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_TN_Mode", false);
                writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TCCS_OM_Mode", false);
            } catch (Exception e) {
                logger.error("call : OPCUA write node failed:", e);
            }
        }
        
        logger.debug("alarmCallable started - monitoring fault");
//        while (processing) {
//            try {
//                handlerNode(opcUaClient, "acienqtags.properties");
//                for (String s : OPCUAWagoHM.OPCUAHm.keySet()) {
//                    if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_C")) {
//                        logger.debug(s + " " + OPCUAWagoHM.OPCUAHm.get(s));
//                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
//                            logger.debug("call - junction clear");
//                            wago.setNewJcSt(wago.getNewJcSt());
//                            processing = false;
//                            // break;
//                        }
//                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_A")) {
//                        logger.debug(s + " " + OPCUAWagoHM.OPCUAHm.get(s));
//                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
//                            logger.debug("call - junction clear");
//                            wago.setNewJcSt(wago.getNewJcSt());
//                            processing = false;
//                            // break;
//                        }
//                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur")) {
//                        logger.debug(s + " " + OPCUAWagoHM.OPCUAHm.get(s));
//                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
//                            logger.debug("call - MI occur");
//                            // wago.setNewJcSt(wago.getNewJcSt());
//                            processing = false;
//                            flagError = true;
//                            // break;
//                        }
//                    }
//
//                }
//            } catch (Exception e) {
//                logger.error("call - OPCUA listener : OPCUA write node failed:", e);
//            }
//            // break;
//        }

        sJsonReturnMsg = "";
        sReplyTransID = "";
        try {
            if (hmDbParam.size() > 0) {
                hmDbParam.clear();
            }
            hmDbParam.put("faultCode", wago.getWagoStatusCode());
            if (wago.getWagoStatusCode() > 0 || flagError) {
                try {
                    wago.setErrorC("Failed");
                    status_code_list statusCodeDBResult = (status_code_list) persistenceManager
                            .findEntity("status_code_list.findByStatusCode", hmDbParam);
                    if (statusCodeDBResult != null) {
                        // wago.setWagoStatusCodeMssg("Rejected: " +
                        // statusCodeDBResult.getStatus_code_mssg());
                        wago.setWagoStatusDesc(statusCodeDBResult.getStatus_desc());
                        wago.setWagoStatusCodeMssg(statusCodeDBResult.getStatus_desc());
                    } else {
                        logger.info(
                                String.format("call - findEntity - Fault Code %s Not Found", wago.getWagoStatusCode()));
                        wago.setWagoStatusCodeMssg("Fault Code Not Found");
                    }
                } catch (Exception e) {
                    logger.error("call - findEntity", e);
                }
            } else {
                wago.setWagoStatusCodeMssg("");
                wago.setErrorC("OK");
            }
            tj_chg_mode_r TjChgModeRDB = wago.getTjChgModeR().generateTjChgModeR(wago.getErrorC(), wago.getWagoStatusCodeMssg(),
                    wago.getCurrTjMode());
            try {
//                persistenceManager.persistEntity(TjChgModeRDB);
            } catch (Exception e) {
                logger.error("call - persist entity : TJ_CHG_MODE_R Data Insert DB Failed", e);
                wago.setWagoStatusCode(303);
            }
            sJsonReturnMsg = wago.getTjChgModeR().generateReturnMsg(TjChgModeRDB);
            sReplyTransID = ConfigWago.getEsbTjChgModeR().toUpperCase();
//            logger.debug("TJ_CHG_MODE_R pub message: \n" + sJsonReturnMsg);
            // Publish out to PSA sollace
            try {
//                publishMessage(sReplyTransID, sJsonReturnMsg);
            } catch (Exception e) {
                logger.error("TJ Chd Mode call - publishMessage : ", e);
            }
        } catch (Exception e2) {
            logger.error("TJ Chg Mode call : " + e2.getMessage());
            // Thread.currentThread().interrupt();
        } finally {
            // Close this thread, job completed.
            // EIUProcess2Thread.stop()
            // Thread.currentThread().interrupt();
        }
        return null;
    }

    public void publishMessage(String transId, String message) {
        try {
            esbClient.tryPublishMessage(transId, message);
        } catch (ESBClientException e) {
            logger.error("TJ CHG MODE R - publishMessage Error : " + e.getMessage(), e);
        }
    }

}
