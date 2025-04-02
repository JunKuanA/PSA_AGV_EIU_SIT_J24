package module.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import module.object.OPCUAWagoHM;
import module.utils.Utils;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.psa.gtosplus.esb.client.ESBClient;
import com.psa.gtosplus.esb.client.exception.ESBClientException;

import module.config.ConfigWago;
import module.entities.equipment_info;
import module.entities.jc_hrtbt_info;
import module.entities.persistenceManager;
import module.message.JcHrtbtInfo;
import module.message.Payload;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import static module.Subscribenode.multipleSubscribe.handlerNode;

public class HBCheck implements Runnable{

    private static Logger logger = Logger.getLogger(HBCheck.class.getName());
    private persistenceManager persistenceManager = null;
    private Thread eiuHBCheckThread = null;
    public static boolean EIU1Alive = true;
    private ESBClient esbClient = null;
    private JcHrtbtInfo jcHrtbtInfo = null;
    private Payload payload = null;
    private Wago wago = null;
    private String sJsonReturnMsg = "";
    private String sReplyTransID = "";
    private Gson gson = null;
    private OpcUaClient opcUaClient = null;
    private int num_of_faults = 0;
    // private int rc = 0;
    
    public HBCheck(persistenceManager persistenceManager, ESBClient esbClient, OpcUaClient opcUaClient,Wago wago) {
        this.persistenceManager = persistenceManager;
        this.esbClient = esbClient;
        this.opcUaClient = opcUaClient;
        // this.wago = new Wago();
        this.wago = wago;
        gson = new Gson();
        String payloadX = "{\"gtosplus_ops_header\":"+
                            "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
                            "\"event_dt\":\"\","+
                            "\"event_id\":\"\","+
                            "\"trans_id\":\"JC_HRTBT_INFO\""+
                            "},"+
                            "\"gtosplus_ops_body\":"+
                            "{\"jc_id\":"+ConfigWago.getJcId()+","+
                            "\"jc_m\":\""+ConfigWago.getJcM()+"\","+
                            "\"control_mode\":\"\""+
                            "}}";

        try {
            payload = gson.fromJson(payloadX, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("HBCheck - Constructor - payloadX: %s", payloadX), e);
            return;
        }
        jcHrtbtInfo = payload.generatePayloadJcHrtbtInfo();
        wago.setJcHrtbtInfo(jcHrtbtInfo);
    }

    public void start() {
        if (eiuHBCheckThread == null) {
            eiuHBCheckThread = new Thread(this, "eiuHBCheckThread");
            eiuHBCheckThread.start();
        }
    }

    public void stop() {
        if (eiuHBCheckThread == null) {
            eiuHBCheckThread = new Thread(this, "eiuHBCheckThread");
            eiuHBCheckThread.interrupt();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                // List<equipment_info> equipmentInfo = persistenceManager.getAllHB();
                // List<Date> equipmentInfoplc = persistenceManager.getPlcLastComm();
                sJsonReturnMsg = "";
                sReplyTransID = "";
                num_of_faults = 0;
                //OPCUA control mode
                handlerNode(opcUaClient,"acienqtags.properties");
                if(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode") == "true"){
                    wago.setControlMode("Remote");
                } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "true"){
                    wago.setControlMode("Local");
                } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Repair_Mode") == "true"){
                    wago.setControlMode("Local");
                } else if ("true".equalsIgnoreCase(wago.getPlcLost())){
                    wago.setControlMode("null");
                    logger.debug("i am here");
                    logger.debug("control mode: " + wago.getControlMode());
                }

                // try {
                //     persistenceManager.updatePLCHB(Integer.parseInt(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PLC_Hearbeat")));
                //     logger.debug("PLC HB: "+OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PLC_Hearbeat"));
                //     //  wago.setControlMode("Remote");
                // }catch(NumberFormatException ne){

                // }
                // catch (Exception e) {
                //     logger.error("HBCheck - run : PLC heartbeat lost", e);
                //     wago.setWagoStatusCode(307);
                // }

                // if(equipmentInfo.get(3).getLast_hb() > equipmentInfo.get(2).getLast_hb()){
                //     EIU1Alive = false;
                //     jc_hrtbt_info jcHrtbtInfoDB = wago.getJcHrtbtInfo().generateJcHrtbtInfo(wago.getControlMode());
                //     try{
                //         persistenceManager.persistEntity(jcHrtbtInfoDB);
                //     }catch (Exception e) {
                //         logger.error("HBCheck - run : JC_HRTBT_INFO Data Insert DB Failed", e);
                //         wago.setWagoStatusCode(303);
                //     }
                //     sJsonReturnMsg = wago.getJcHrtbtInfo().generateReturnMsg(jcHrtbtInfoDB);
                //     sReplyTransID = ConfigWago.getEsbJcHrtbtInfo().toUpperCase();
                //     logger.debug("JC_HRTBT_INFO pub message: \n" + sJsonReturnMsg);
                //     // Publish out to PSA sollace
                //     try{
                //         publishMessage(sReplyTransID, sJsonReturnMsg);
                //     }catch (Exception e) {
                //         logger.info("HBCheck - run : ", e);
                //     }
                // }else{
                //     EIU1Alive = true;
                //     logger.info(equipmentInfo.get(2).getEqpt_name() + " >= " + equipmentInfo.get(3).getEqpt_name());
                //     logger.info(equipmentInfo.get(2).getLast_hb() + " >= " + equipmentInfo.get(3).getLast_hb());
                //     jc_hrtbt_info jcHrtbtInfoDB = wago.getJcHrtbtInfo().generateJcHrtbtInfo(wago.getControlMode());
                //     try{
                //         persistenceManager.persistEntity(jcHrtbtInfoDB);
                //     }catch (Exception e) {
                //         logger.error("HBCheck - run : JC_HRTBT_INFO Data Insert DB Failed", e);
                //         wago.setWagoStatusCode(303);
                //     }
                //     sJsonReturnMsg = wago.getJcHrtbtInfo().generateReturnMsg(jcHrtbtInfoDB);
                //     sReplyTransID = ConfigWago.getEsbJcHrtbtInfo().toUpperCase();
                //     logger.debug("JC_HRTBT_INFO pub message: \n" + sJsonReturnMsg);
                //     // Publish out to PSA sollace
                //     try{
                //         publishMessage(sReplyTransID, sJsonReturnMsg);
                //     }catch (Exception e) {
                //         logger.info("HBCheck - run : ", e);
                //     }
                // }

                // jc_hrtbt_info jcHrtbtInfoDB = wago.getJcHrtbtInfo().generateJcHrtbtInfo(wago.getControlMode());
                //     try{
                //         persistenceManager.persistEntity(jcHrtbtInfoDB);
                //     }catch (Exception e) {
                //         logger.error("HBCheck - run : JC_HRTBT_INFO Data Insert DB Failed", e);
                //         wago.setWagoStatusCode(303);
                //     }
                //     sJsonReturnMsg = wago.getJcHrtbtInfo().generateReturnMsg(jcHrtbtInfoDB);
                //     sReplyTransID = ConfigWago.getEsbJcHrtbtInfo().toUpperCase();
                //     logger.debug("JC_HRTBT_INFO pub message: \n" + sJsonReturnMsg);
                //     // Publish out to PSA sollace
                //     try{
                //         publishMessage(sReplyTransID, sJsonReturnMsg);
                //     }catch (Exception e) {
                //         logger.info("HBCheck - run : ", e);
                //     }
                // Thread.sleep(60 * 1000L);
                jc_hrtbt_info jcHrtbtInfoDB = wago.getJcHrtbtInfo().generateJcHrtbtInfo(wago.getControlMode());
                try{
                    // publishMessage(sReplyTransID, sJsonReturnMsg);
                    sJsonReturnMsg = wago.getJcHrtbtInfo().generateReturnMsg(jcHrtbtInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcHrtbtInfo().toUpperCase();
                    logger.debug("JC_HRTBT_INFO pub message: \n" + sJsonReturnMsg);
                    int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                    if (a == 0){
                        try{
                            persistenceManager.persistEntity(jcHrtbtInfoDB);
                            logger.debug("insert successfully");
                        }catch (Exception e) {
                            logger.error("HBCheck - run : JC_HRTBT_INFO Data Insert DB Failed", e);
                            wago.setWagoStatusCode(303);
                        }
                    }else{
                        // logger.error("system exit hrtbt");
                        // System.exit(1);
                        logger.info("system unable to publish, reconnection attempted");
                        esbClient.stopEsbClient();
                        esbClient.startEsbClient();
                        }
                }catch (Exception e) {
                    logger.info("HBCheck - run : ", e);
                }
            Thread.sleep(20 * 1000L);
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
