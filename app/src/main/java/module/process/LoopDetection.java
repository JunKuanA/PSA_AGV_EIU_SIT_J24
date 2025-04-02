package module.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import module.object.OPCUAWagoHM;
import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import com.google.gson.Gson;
import com.psa.gtosplus.esb.client.ESBClient;
import com.psa.gtosplus.esb.client.exception.ESBClientException;

import module.config.ConfigWago;
import module.entities.jc_pm_dtec_info;
import module.entities.jc_uturn_dtec_info;
import module.entities.persistenceManager;
import module.message.JcPmDtecInfo;
import module.message.JcUturnDtecInfo;
import module.message.Payload;

import static module.Subscribenode.multipleSubscribe.handlerNode;

public class LoopDetection implements Runnable{

    private static Logger logger = Logger.getLogger(LoopDetection.class.getName());
    private persistenceManager persistenceManager = null;
    private Thread loopDetectionThread = null;
    private ESBClient esbClient = null;
    private JcPmDtecInfo jcPmDtecInfoM = null;
    private JcUturnDtecInfo jcUturnDtecInfoM = null;
    private int num_of_pm = 0;
    private int num_of_uturn = 0;
    private List<String> pm_loop_list = null;
    private List<String> uturn_loop_list = null;
    private Wago wago = null;
    private String sJsonReturnMsg = "";
    private String sReplyTransID = "";
    private Payload payloadLoop = null;
    private Gson gsonLoop = null;
    private Payload payloadUturn = null;
    private Gson gsonUturn = null;
    private OpcUaClient opcUaClient = null;
    private HashMap<String, Object> hmDbParam = null;

    public LoopDetection(persistenceManager persistenceManager,ESBClient esbClient, HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient) {
        this.persistenceManager = persistenceManager;
        this.esbClient = esbClient;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        if (hmDbParam.size()>0){hmDbParam.clear();}
        gsonLoop = new Gson();
        String payloadXLoop = "{\"gtosplus_ops_header\": "+
                "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_PM_DTEC_INFO\""+
                "},"+
                "\"gtosplus_ops_body\":"+
                "{\"jc_id\":"+ConfigWago.getJcId()+","+
                "\"jc_m\":\""+ConfigWago.getJcM()+"\","+
                "\"num_of_pm_loop\":0,"+
                "\"pm_loop_list\":"+
                "[]}}";
        try {
            payloadLoop = gsonLoop.fromJson(payloadXLoop, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("LoopDetection - Constructor - payloadXLoop: %s", payloadXLoop), e);
            return;
        }

        gsonUturn = new Gson();
        String payloadXUturn = "{\"gtosplus_ops_header\": "+
                "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_UTURN_DTEC_INFO\""+
                "},"+
                "\"gtosplus_ops_body\":"+
                "{\"jc_id\":"+ConfigWago.getJcId()+","+
                "\"jc_m\":\""+ConfigWago.getJcM()+"\","+
                "\"num_of_uturn_loop\":0,"+
                "\"uturn_loop_list\":"+
                "[]}}";
        try {
            payloadUturn = gsonUturn.fromJson(payloadXUturn, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("LoopDetection - Constructor - payloadXUturn: %s", payloadXUturn), e);
            return;
        }
    }

    public LoopDetection(persistenceManager persistenceManager, HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient) {
        this.persistenceManager = persistenceManager;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        if (hmDbParam.size()>0){hmDbParam.clear();}
        gsonLoop = new Gson();
        String payloadXLoop = "{\"gtosplus_ops_header\": "+
                "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_PM_DTEC_INFO\""+
                "},"+
                "\"gtosplus_ops_body\":"+
                "{\"jc_id\":"+ConfigWago.getJcId()+","+
                "\"jc_m\":\""+ConfigWago.getJcM()+"\","+
                "\"num_of_pm_loop\":0,"+
                "\"pm_loop_list\":"+
                "[]}}";
        try {
            payloadLoop = gsonLoop.fromJson(payloadXLoop, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("LoopDetection - Constructor - payloadXLoop: %s", payloadXLoop), e);
            return;
        }

        gsonUturn = new Gson();
        String payloadXUturn = "{\"gtosplus_ops_header\": "+
                "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_UTURN_DTEC_INFO\""+
                "},"+
                "\"gtosplus_ops_body\":"+
                "{\"jc_id\":"+ConfigWago.getJcId()+","+
                "\"jc_m\":\""+ConfigWago.getJcM()+"\","+
                "\"num_of_uturn_loop\":0,"+
                "\"uturn_loop_list\":"+
                "[]}}";
        try {
            payloadUturn = gsonUturn.fromJson(payloadXUturn, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("LoopDetection - Constructor - payloadXUturn: %s", payloadXUturn), e);
            return;
        }
    }

    public void start() {
        if (loopDetectionThread == null) {
            loopDetectionThread = new Thread(this, "loopDetectionThread");
            loopDetectionThread.start();
        }
    }

    public void stop() {
        if (loopDetectionThread == null) {
            loopDetectionThread = new Thread(this, "loopDetectionThread");
            loopDetectionThread.interrupt();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                jcPmDtecInfoM = null;
                num_of_pm = 0;
                pm_loop_list = new ArrayList<String>();
                jcUturnDtecInfoM = null;
                num_of_uturn = 0;
                uturn_loop_list = new ArrayList<String>();
                wago = new Wago();

                sJsonReturnMsg = "";
                sReplyTransID = "";
                //OPCUA Listening Pm loop detection and Uturn loop detection
                handlerNode(opcUaClient,"acilooptags.properties");

                for (String s : OPCUAWagoHM.OPCUAHm.keySet()) {
                    if(s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_EWT_Loop")){
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            pm_loop_list.add("{\"pm_loop_m\": \""+"EWT_Loop"+"\",\"active_i\": "+"true"+"}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false"){
                            pm_loop_list.add("{\"pm_loop_m\": \""+"EWT_Loop"+"\",\"active_i\": "+"false"+"}");
                        }
                    } else if(s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_NT_Loop")){
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            pm_loop_list.add("{\"pm_loop_m\": \""+"NT_Loop"+"\",\"active_i\": "+"true"+"}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false"){
                            pm_loop_list.add("{\"pm_loop_m\": \""+"NT_Loop"+"\",\"active_i\": "+"false"+"}");
                        }
                    } else if(s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_ST_Loop")){
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            pm_loop_list.add("{\"pm_loop_m\": \""+"ST_Loop"+"\",\"active_i\": "+"true"+"}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false"){
                            pm_loop_list.add("{\"pm_loop_m\": \""+"ST_Loop"+"\",\"active_i\": "+"false"+"}");
                        }
                    }  else if(s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_EWT_Loop")){
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            uturn_loop_list.add("{\"uturn_loop_m\": \""+"EWT_Loop"+"\",\"active_i\": "+"true"+"}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false"){
                            uturn_loop_list.add("{\"uturn_loop_m\": \""+"EWT_Loop"+"\",\"active_i\": "+"false"+"}");
                        }
                    }
                }

                num_of_pm = pm_loop_list.size();
                sJsonReturnMsg = "";
                sReplyTransID = "";
                logger.info("Inside LoopDetection Thraed");
                logger.info(String.format("Inside LoopDetectionDtec Thraed:     OPC_UA.TL_PH_D = " + OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_D")));
                logger.info(String.format("Inside LoopDetection Thraed:     OPC_UA.TL_ST_Loop = " + OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_ST_Loop")));
                logger.info(String.format("Inside LoopDetection Thraed:     OPC_UA.TL_EWT_Loop = " + OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_EWT_Loop")));
                logger.info(String.format("Inside LoopDetection Thraed:     OPC_UA.TL_NT_Loop = " + OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_NT_Loop")));
                if(num_of_pm > 0 && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_D") == "true" && ((OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_EWT_Loop") == "true" || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_NT_Loop") == "true" || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_ST_Loop") == "true"))){
                    jcPmDtecInfoM = payloadLoop.generatePayloadJcPmDtecInfo();
                    wago.setJcPmDtecInfo(jcPmDtecInfoM);
                    jc_pm_dtec_info jcPmDtecInfoDB = wago.getJcPmDtecInfo().generateJcPmDtecInfo(num_of_pm,pm_loop_list);
                    // try{
                    //     persistenceManager.persistEntity(jcPmDtecInfoDB);
                    // }catch (Exception e) {
                    //     logger.error("LoopDetection - run - PM: JC_PM_DTEC_INFO Data Insert DB Failed", e);
                    //     wago.setWagoStatusCode(303);
                    // }
                    sJsonReturnMsg = wago.getJcPmDtecInfo().generateReturnMsg(jcPmDtecInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcPmDtecInfo().toUpperCase();
                    logger.debug("JC_PM_DTEC_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                        if(a==0){
                        try{
                            persistenceManager.persistEntity(jcPmDtecInfoDB);
                        }catch (Exception e) {
                            logger.error("LoopDetection - run - PM: JC_PM_DTEC_INFO Data Insert DB Failed", e);
                            wago.setWagoStatusCode(303);
                        }
                    }else {
                        // logger.error("system exit loop");
                        // System.exit(1);
                        logger.info("system unable to publish, reconnection attempted");
                        esbClient.stopEsbClient();
                        esbClient.startEsbClient();
                    }
                    }catch (Exception e) {
                        logger.info("LoopDetection - run : ", e);
                    }
                }
                Thread.sleep(60 * 1000L);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // every 30 second
        }
    }

    public int publishMessage(String transId, String message) {
        int rc = 0;
        try {
            rc = esbClient.tryPublishMessage(transId, message);
            if (rc != 0){
                logger.error("Failed to send message, return code " + rc);
                // esbClient.stopEsbClient();
                // try {
                //     Thread.sleep(1000);
                //     if (esbClient.startEsbClient() == -1) {
                //         System.exit(1);
                //     }
                // } catch (InterruptedException e) {
                //     // Handle the exception (e.g., log it)
                //     e.printStackTrace();
                // }
            }
        } catch (ESBClientException e) {
            logger.error("EIUProcessStep1 - publishMessage Error : " + e.getMessage(), e);
        }
        logger.debug("rc value: " + rc);
        return rc;
    }

}
