package module.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

public class AlarmPubThreadCallable implements Callable<Void>{

    private static Logger logger = Logger.getLogger(AlarmPubThread.class.getName());
    private persistenceManager persistenceManager = null;
    private Thread alarmThread = null;
    public static boolean PLCAlive = true;
    private ESBClient esbClient = null;
    private JcAlrmInfo jcAlrmInfoM = null;
    private int num_of_faults = 0;
    private List<String> fault_desc_list = null;
    private Wago wago = null;
    private String sJsonReturnMsg = "";
    private String sReplyTransID = "";
    private Payload payload = null;
    private Gson gson = null;
    private OpcUaClient opcUaClient = null;
    private HashMap<String, Object> hmDbParam = null;
    // private HashMap<String, Object> plcDbParam = null;
    private boolean taskIncomplete = false;
    private boolean miOccured = true;
    private boolean dgrdmode = false;
    private boolean mamode =false;
    private boolean plcdown =false;
    private boolean plcerrorlock =true;
    private persistenceManager dbCtrl = null;

    public AlarmPubThreadCallable(persistenceManager persistenceManager,ESBClient esbClient, HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient, Wago wago) {
        this.persistenceManager = persistenceManager;
        this.esbClient = esbClient;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        if (hmDbParam.size()>0){hmDbParam.clear();}
        // this.wago = new Wago();
        this.wago = wago;
        gson = new Gson();
        String payloadX = "{\"gtosplus_ops_header\": "+
                "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_ALRM_INFO\""+
                "},"+
                "\"gtosplus_ops_body\":"+
                "{\"jc_id\":"+ConfigWago.getJcId()+","+
                "\"jc_m\":\""+ConfigWago.getJcM()+"\","+
                "\"alert_mode\":\"\","+
                "\"mi_i\":false,"+
                "\"num_of_faults\":0,"+
                "\"fault_code_desc_list\":"+
                "[]}}";
        try {
            payload = gson.fromJson(payloadX, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("AlarmPubThread - Constructor - payloadX: %s", payloadX), e);
            return;
        }
        jcAlrmInfoM = payload.generatePayloadJcAlrmInfo();
        wago.setJcAlrmInfo(jcAlrmInfoM);
    }

    public AlarmPubThreadCallable(persistenceManager persistenceManager,HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient) {
        this.persistenceManager = persistenceManager;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        this.wago = new Wago();
        gson = new Gson();
        String payloadX = "{\"gtosplus_ops_header\": "+
                "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_ALRM_INFO\""+
                "},"+
                "\"gtosplus_ops_body\":"+
                "{\"jc_id\":"+ConfigWago.getJcId()+","+
                "\"jc_m\":\""+ConfigWago.getJcM()+"\","+
                "\"alert_mode\":\"\","+
                "\"mi_i\":false,"+
                "\"num_of_faults\":0,"+
                "\"fault_code_desc_list\":"+
                "[]}}";
        try {
            payload = gson.fromJson(payloadX, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("AlarmPubThread - Constructor - payloadX: %s", payloadX), e);
            return;
        }
        jcAlrmInfoM = payload.generatePayloadJcAlrmInfo();
        wago.setJcAlrmInfo(jcAlrmInfoM);
    }

    // public void start() {
    //     if (alarmThread == null) {
    //         alarmThread = new Thread(this, "alarmThread");
    //         alarmThread.start();
    //     }
    // }

    // public void stop() {
    //     if (alarmThread == null) {
    //         alarmThread = new Thread(this, "alarmThread");
    //         alarmThread.interrupt();
    //     }
    // }

    // private void checkInfo() {
    //     /*
    //      * Check junction information before executing instruction
    //      */
       
    //     if (hmDbParam.size()>0){hmDbParam.clear();}
    //     hmDbParam.put("eqpt_name", String.valueOf("plc"));
    //     equipment_info equipmentInfoDBResult = (equipment_info) dbCtrl.findEntity("equipment_info.findByEqptName", hmDbParam);
    
    //     if (!Utils.checkDateWithInTolerance(equipmentInfoDBResult.getLast_comm(), ConfigWago.getMaxHeartbeatTolerance())) {
    //         fault_desc_list.add("{\"fault_code\": "+2408+",\"subcompo_m\": \""+"PLC Lost Comm"+"\",\"active_i\": "+"true"+"}");
    //         logger.error("pubCmdToWago - execJcUpd - PLC is lost comm since "+equipmentInfoDBResult.getLast_comm());
    //         wago.setWagoStatusCode(2408); // PLC communication lost
    //         num_of_faults++;
    //         this.plcdown=true;
    //     }
    // }

    @Override
    public Void call() throws Exception {
        // while(true){
            try {
                sJsonReturnMsg = "";
                sReplyTransID = "";
                num_of_faults = 0;
                fault_desc_list = null;
                
                // if (hmDbParam.size()>0){hmDbParam.clear();}
                // hmDbParam.put("eqpt_name", String.valueOf("plc"));
                // equipment_info equipmentInfoDBResult = (equipment_info) dbCtrl.findEntity("equipment_info.findByEqptName", hmDbParam);

                //OPCUA Listening all equipment status
                handlerNode(opcUaClient,"acimitags.properties");
                fault_desc_list = new ArrayList<>();
                for (String s : OPCUAWagoHM.OPCUAHm.keySet()) {
                    if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Healthy")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +80404+ ",\"subcompo_m\": \"" + "Traffic_Light" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Traffic Light ) is having fault");
                            num_of_faults++;
                        }
                        //power supply
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_PSU_A_Health")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "false"){
                            fault_desc_list.add("{\"fault_code\": "+70404+",\"subcompo_m\": \""+"Power_Supply_A"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Power Supply A ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_PSU_B_Health")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "false"){
                            fault_desc_list.add("{\"fault_code\": "+70404+",\"subcompo_m\": \""+"Power_Supply_B"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Power Supply B ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_PSU_C_Health")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "false"){
                            fault_desc_list.add("{\"fault_code\": "+70404+",\"subcompo_m\": \""+"Power_Supply_C"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Power Supply C ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_PSU_D_Health")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "false"){
                            fault_desc_list.add("{\"fault_code\": "+70404+",\"subcompo_m\": \""+"Power_Supply_D"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Power Supply D ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_UPS_Health")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+70404+",\"subcompo_m\": \""+"UPS"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( UPS ) is having fault");
                            num_of_faults++;
                        }
                        //barriers
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+51410+",\"subcompo_m\": \""+"Barrier01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+51410+",\"subcompo_m\": \""+"Barrier01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51404+ ",\"subcompo_m\": \"" + "Barrier01" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_002_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier02" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_002_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier02" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_002_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51404+ ",\"subcompo_m\": \"" + "Barrier02" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier03" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier03" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51404+ ",\"subcompo_m\": \"" + "Barrier03" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 03 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_004_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier04" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_004_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier04" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_004_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51404+ ",\"subcompo_m\": \"" + "Barrier04" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier05" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 05 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier05" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 05 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51404+ ",\"subcompo_m\": \"" + "Barrier05" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 05 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_006_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier06" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_006_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier06" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_006_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51404+ ",\"subcompo_m\": \"" + "Barrier06" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier07" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 07 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier07" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 07 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier07" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 07 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_008_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51404+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_008_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_008_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                        //line sensor
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Line_001_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Line_002_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Line_003_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor03"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 03 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Line_004_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor04"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Line_005_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor05"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 05 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Line_006_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor06"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 06 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Line_007_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor07"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 07 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Line_008_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor08"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 08 ) is having fault");
                            num_of_faults++;
                        }
                        //barrier loop
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Loop_001_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Loop_002_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Loop_003_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop03"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 03 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Loop_004_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop04"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Loop_005_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop05"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 05 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Loop_006_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop06"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 06 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Loop_007_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop07"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 07 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Loop_008_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop08"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 08 ) is having fault");
                            num_of_faults++;
                        }
                        //lidar sensor
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_SW_Z1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_SW_Z2")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_Alarm_1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+41412+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_002_SW_Z1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_002_SW_Z2")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_002_Alarm_1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+41412+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    // }else if (!Utils.checkDateWithInTolerance(equipmentInfoDBResult.getLast_comm(), ConfigWago.getMaxHeartbeatTolerance())) {
                    //     fault_desc_list.add("{\"fault_code\": "+2408+",\"subcompo_m\": \""+"PLC Lost Comm"+"\",\"active_i\": "+"true"+"}");
                    //     logger.error("pubCmdToWago - execJcUpd - PLC is lost comm since "+equipmentInfoDBResult.getLast_comm());
                    //     wago.setWagoStatusCode(2408); // PLC communication lost
                    //     num_of_faults++;
                    //     this.plcdown=true;
                    }
                }

                List<Date> equipmentInfo = persistenceManager.getPlcLastComm();
                if(!Utils.checkDateWithInTolerance(equipmentInfo.get(0), ConfigWago.getMaxHeartbeatTolerance())){
                logger.debug("last comm time:"+equipmentInfo.get(0));
                plcdown = true;
                fault_desc_list.add("{\"fault_code\": "+307+",\"subcompo_m\": \""+"PLC Lost Communication"+"\",\"active_i\": "+"true"+"}");
                logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component PLC Lost Communication");
                num_of_faults++;
                // List<equipment_info> equipmentInfo = persistenceManager.getAllHB();
                }

                if(num_of_faults > 0){
                    
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("true","Critical",
                            num_of_faults,fault_desc_list);
                    try{
                        persistenceManager.persistEntity(jcAlrmInfoDB);
                    }catch (Exception e) {
                        logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                        wago.setWagoStatusCode(303);
                    }
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        publishMessage(sReplyTransID, sJsonReturnMsg);
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }else if((num_of_faults > 0 || num_of_faults == 0) && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "true"){
                    
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("false","Warning",
                            num_of_faults,fault_desc_list);
                    try{
                        persistenceManager.persistEntity(jcAlrmInfoDB);
                    }catch (Exception e) {
                        logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                        wago.setWagoStatusCode(303);
                    }
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        publishMessage(sReplyTransID, sJsonReturnMsg);
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }else if((num_of_faults > 0 || num_of_faults == 0) && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "true"){
                   
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("true","E-Critical",
                            num_of_faults,fault_desc_list);
                    try{
                        persistenceManager.persistEntity(jcAlrmInfoDB);
                    }catch (Exception e) {
                        logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                        wago.setWagoStatusCode(303);
                    }
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        publishMessage(sReplyTransID, sJsonReturnMsg);
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }else if(num_of_faults == 0 && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur") == "false" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "false" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode") == "true"){
                    
                    this.plcerrorlock = true;
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("false","OK",
                            num_of_faults,fault_desc_list);
                    try{
                        persistenceManager.persistEntity(jcAlrmInfoDB);
                    }catch (Exception e) {
                        logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                        wago.setWagoStatusCode(303);
                    }
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        publishMessage(sReplyTransID, sJsonReturnMsg);
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }else if(num_of_faults > 0 && plcdown){
                    this.plcdown = false;
                    
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("true","Critical",
                            num_of_faults,fault_desc_list);
                    try{
                        persistenceManager.persistEntity(jcAlrmInfoDB);
                    }catch (Exception e) {
                        logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                        wago.setWagoStatusCode(305);
                    }
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        publishMessage(sReplyTransID, sJsonReturnMsg);
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }
                // Thread.sleep(1 * 1000L);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // every 30 second
        // }
        return null;
    }

    public void publishMessage(String transId, String message) {
        try {
            esbClient.tryPublishMessage(transId, message);
        } catch (ESBClientException e) {
            logger.error("LoopDetection - publishMessage Error : " + e.getMessage(), e);
        }
    }

}
