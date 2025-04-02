package module.process;

import com.google.gson.Gson;
import com.psa.gtosplus.esb.client.ESBClient;
import com.psa.gtosplus.esb.client.exception.ESBClientException;
import module.config.ConfigWago;
import module.entities.jc_ack_r;
import module.entities.jc_enq_info_r;
import module.entities.persistenceManager;
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

import static module.Subscribenode.multipleSubscribe.handlerNode;

public class JcAckRCallable implements Callable<Void> {
    private static Logger logger = Logger.getLogger(JcAckRCallable.class.getName());
    private module.entities.persistenceManager persistenceManager = null;
    private ESBClient esbClient = null;
    private JcAckR jcAckRM = null;
    private Wago wago = null;
    private String sJsonReturnMsg = "";
    private String sReplyTransID = "";
    private Payload payload = null;
    private Gson gson = null;
    private OpcUaClient opcUaClient = null;
    private HashMap<String, Object> hmDbParam = null;
    private String error_c = "";
    private String error_txt="";
    private String prev_trans_id="";

    public JcAckRCallable(persistenceManager persistenceManager, ESBClient esbClient, HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient, Wago wago) {
        this.persistenceManager = persistenceManager;
        this.esbClient = esbClient;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        this.wago = wago;
        if (hmDbParam.size()>0){hmDbParam.clear();}
        gson = new Gson();
        String payloadX = "{\"gtosplus_ops_header\":"+
                "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_ACK_R\""+
                "},"+
                "\"gtosplus_ops_body\":"+
                "{\"error_c\":\"\""+
                ",\"error_txt\":\"\""+
                ",\"jc_id\":"+ConfigWago.getJcId()+
                ",\"jc_m\":\""+ConfigWago.getJcM()+
                "\",\"prev_trans_id\":\"\"}}";
        try {
            payload = gson.fromJson(payloadX, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("Constructor - payloadX: %s", payloadX), e);
            return;
        }
        jcAckRM = payload.generatePayloadJcAckR();
        wago.setJcAckR(jcAckRM);
    }

    public JcAckRCallable(module.entities.persistenceManager persistenceManager, OpcUaClient opcUaClient, HashMap<String, Object> hmDbParam, Wago wago) {
        this.persistenceManager = persistenceManager;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        this.wago = wago;
        gson = new Gson();

        String payloadX = "{\"gtosplus_ops_header\":"+
                "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_ACK_R\""+
                "},"+
                "\"gtosplus_ops_body\":"+
                "{\"error_c\":\"\""+
                ",\"error_txt\":\"\""+
                ",\"jc_id\":"+ConfigWago.getJcId()+
                ",\"jc_m\":\""+ConfigWago.getJcM()+
                "\",\"prev_trans_id\":\"\"}}";
        try {
            payload = gson.fromJson(payloadX, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("Constructor - payloadX: %s", payloadX), e);
            return;
        }
        jcAckRM = payload.generatePayloadJcAckR();
        wago.setJcAckR(jcAckRM);
    }

    @Override
    public Void call() throws Exception  {
            try {
                error_c = "";
                error_txt = "";
                prev_trans_id = "";
                sJsonReturnMsg = "";
                sReplyTransID = "";
                // if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "true"){
                //     // wago.setWagoStatusCodeMssg("");
                //     wago.setErrorC("Warning");
                // }else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "false"){
                //     // wago.setWagoStatusCodeMssg("");
                //     wago.setErrorC("OK");
                // }
                jc_ack_r jcAckRDB = wago.getJcAckR().generateJcAckR(wago.getErrorC(), wago.getWagoStatusCodeMssg(), "JC_UPD_ST");
                try{
                    persistenceManager.persistEntity(jcAckRDB);
                    logger.debug("JC_ACK_R Data Insert DB Successfully");
                }catch (Exception e) {
                    logger.error("call : JC_ACK_R Data Insert DB Failed", e);
                    wago.setWagoStatusCode(303);
                }
                sJsonReturnMsg = wago.getJcAckR().generateAckReturnMsg(jcAckRDB);
                sReplyTransID = ConfigWago.getEsbJcAckR().toUpperCase();
                logger.debug("JC_ACK_R pub message: \n" + sJsonReturnMsg);
                // Publish out to PSA sollace
                try {
                    publishMessage(sReplyTransID, sJsonReturnMsg);
                } catch (Exception e) {
                    logger.info("ACK call - publishMessage : ", e);
                }
                // Thread.sleep(10 * 1000L); //only for debug purpose
                // return null;
            } catch (Exception e2) {
                logger.error("ACK call : " + e2.getMessage());
            }
        return null;
    }
    public void publishMessage(String transId, String message) {
        try {
            esbClient.tryPublishMessage(transId, message);
        } catch (ESBClientException e) {
            logger.error("Jc ACK R - publishMessage Error : " + e.getMessage(), e);
        }
    }

}
