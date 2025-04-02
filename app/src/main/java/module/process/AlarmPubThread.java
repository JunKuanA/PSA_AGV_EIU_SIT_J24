package module.process;

import java.net.InetAddress;
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

public class AlarmPubThread implements Runnable{

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
    private boolean taskIncomplete = false;
    private boolean miOccured = true;
    private boolean dgrdmode = false;
    private boolean mamode =false;
    private boolean plcdown =false;
    private boolean plcerrorlock =true;
    private persistenceManager dbCtrl = null;
    private boolean plcrecovered = false;

    public AlarmPubThread(persistenceManager persistenceManager,ESBClient esbClient, HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient, Wago wago) {
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

    public AlarmPubThread(persistenceManager persistenceManager,HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient) {
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

    public void start() {
        if (alarmThread == null) {
            alarmThread = new Thread(this, "alarmThread");
            alarmThread.start();
        }
    }

    public void stop() {
        if (alarmThread == null) {
            alarmThread = new Thread(this, "alarmThread");
            alarmThread.interrupt();
        }
    }

    @Override
    public void run() {
        while(true){
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
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_002_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier02" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_002_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier02" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_002_DI_Hty")) {
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
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_004_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier04" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_004_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier04" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_004_DI_Hty")) {
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
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_006_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier06" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_006_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier06" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_006_DI_Hty")) {
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
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_008_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51404+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_008_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_008_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_009_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 09 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_009_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 09 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_009_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 09 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_010_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 10 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_010_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 10 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_010_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 10 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_011_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 11 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_011_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 11 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_011_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 11 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }
                    //line sensor
                    else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Line_001_Detected")) {
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
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_Line_009_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor08"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 09 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_Line_010_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor07"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 10 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_Line_011_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor08"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 11 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_Line_012_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+60405+",\"subcompo_m\": \""+"Line_Sensor08"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 12 ) is having fault");
                            num_of_faults++;
                        }
                    }

                    //barrier loop
                    else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Loop_001_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Loop_002_Alarm")) {
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
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003AB_Loop_004_Alarm")) {
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
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Loop_006_Alarm")) {
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
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_Loop_009_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop05"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 09 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_Loop_010_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop06"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 10 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_Loop_011_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop07"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 11 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_Loop_012_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+59404+",\"subcompo_m\": \""+"Barrier_Loop08"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 12 ) is having fault");
                            num_of_faults++;
                        }
                    }
                    //lidar sensor
                    else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_SW_Z1")) {
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
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_002_SW_Z1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_002_SW_Z2")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_002_Alarm_1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+41412+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_003_SW_Z1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 03 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_003_SW_Z2")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 03 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_003_Alarm_1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+41412+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 03 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005_Lidar_004_SW_Z1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005_Lidar_004_SW_Z2")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+40405+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005_Lidar_004_Alarm_1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+41412+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 04 ) is having fault");
                            num_of_faults++;
                        }
                    }
                }

                InetAddress geek = InetAddress.getByName("10.106.160.1");
                // System.out.println("Sending Ping Request to " + ipAddress);
                boolean isReachable = geek.isReachable(10000);
                if(!isReachable){
                // logger.info("10.106.160.1" + " is " + (isReachable ? "reachable" : "not reachable"));
                plcdown = true;
                fault_desc_list.add("{\"fault_code\": "+307+",\"subcompo_m\": \""+"PLC Lost Communication"+"\",\"active_i\": "+"true"+"}");
                logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component PLC Lost Communication");
                num_of_faults++;
                wago.setPlcLost("true");
                }else{
                        wago.setPlcLost("false");
                        logger.info("plc reachable");
                    // try {
                    //     persistenceManager.updatePLCHB(Integer.parseInt(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PLC_Hearbeat")));
                    //     logger.debug("PLC HB: "+OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PLC_Hearbeat"));
                    // }catch(NumberFormatException ne){
    
                    // }
                    // catch (Exception e) {
                    //     logger.error("HBPublishThread - run : PLC heartbeat lost", e);
                    // }
                    List<equipment_info> equipmentInfo = persistenceManager.getAllHB();
                    int heartbeatRunningNumber = equipmentInfo.get(4).getLast_hb();
                    heartbeatRunningNumber = (heartbeatRunningNumber + 1) % 10000;
                    try {
                        persistenceManager.updatePLCHB(heartbeatRunningNumber);
                    } catch (Exception e) {
                        logger.error("HBPublishThread - run : EIU heartbeat lost", e);
                    }
                }

                // List<Date> equipmentInfo = persistenceManager.getPlcLastComm();
                // if(!Utils.checkDateWithInTolerance(equipmentInfo.get(0), ConfigWago.getInfoTolerance())){
                // logger.debug("last comm time:"+equipmentInfo.get(0));
                // plcdown = true;
                // fault_desc_list.add("{\"fault_code\": "+307+",\"subcompo_m\": \""+"PLC Lost Communication"+"\",\"active_i\": "+"true"+"}");
                // logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component PLC Lost Communication");
                // num_of_faults++;
                // // List<equipment_info> equipmentInfo = persistenceManager.getAllHB();
                // }

                // HashMap<String, Object> alrmDbParam = new HashMap<>();
                // if (alrmDbParam.size()>0){
                //     alrmDbParam.clear();
                // }

                // alrmDbParam.put("fault_code_desc_list", fault_desc_list);

                // jc_alrm_info jcAlrmDBResult = null;
                // try {
                //     jcAlrmDBResult = (jc_alrm_info) persistenceManager.findEntity("jc_alrm_info.findByfault", alrmDbParam);
                // } catch (NullPointerException npe) {
                //     logger.error("NullPointerException occurred: " + npe.getMessage());
                //     npe.printStackTrace(); // Print the stack trace for further investigation
                //     // Handle the exception as needed
                // }
                
                // jc_alrm_info jcAlrmDBResult = (jc_alrm_info) dbCtrl.getLatestSingleResult("jc_alrm_info.findByfault", alrmDbParam);
                // logger.debug("outside jcalrmdbresult " + jcAlrmDBResult);
                // logger.debug("outside fault code desc list" + fault_desc_list);
                // logger.debug("outside alrmdb param" + alrmDbParam);
                
                // if(jcAlrmDBResult != null){
                //     miOccured=false;
                // }else{
                //     miOccured=true;
                // }


                if(num_of_faults > 0 && miOccured && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "false" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur") == "true"){
                    logger.info("mioccured in fault" + miOccured);
                    this.taskIncomplete = true;
                    // this.miOccured = false;
                    this.dgrdmode = true;
                    this.mamode = true;
                    logger.info("mioccured after mi false" + miOccured);
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("true","Critical",
                            num_of_faults,fault_desc_list);
                    
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                        if (a == 0){
                            try{
                                persistenceManager.persistEntity(jcAlrmInfoDB);
                            }catch (Exception e) {
                                logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                                wago.setWagoStatusCode(303);
                            }
                        }else{
                            // logger.error("system exit alrm");
                            // System.exit(1);
                            logger.info("system unable to publish, reconnection attempted");
                            esbClient.stopEsbClient();
                            esbClient.startEsbClient();
                        }
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }else if((num_of_faults > 0 || num_of_faults == 0) && dgrdmode && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "true" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur") == "false" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode") == "true"){
                    this.taskIncomplete  = true;
                    this.dgrdmode = false;
                    this.miOccured = true;
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("false","Warning",
                            num_of_faults,fault_desc_list);
                    
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                        if (a == 0){
                            try{
                                persistenceManager.persistEntity(jcAlrmInfoDB);
                            }catch (Exception e) {
                                logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                                wago.setWagoStatusCode(303);
                            }
                        }else{
                            // logger.error("system exit alrm");
                            // System.exit(1);
                            logger.info("system unable to publish, reconnection attempted");
                            esbClient.stopEsbClient();
                            esbClient.startEsbClient();
                        }
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }else if((num_of_faults > 0 || num_of_faults == 0) && mamode && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "true"){
                    this.taskIncomplete  = true;
                    this.mamode = false;
                    this.miOccured = true;
                    this.dgrdmode = true;
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("true","E-Critical",
                            num_of_faults,fault_desc_list);
                    
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                        if (a == 0){
                            try{
                                persistenceManager.persistEntity(jcAlrmInfoDB);
                            }catch (Exception e) {
                                logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                                wago.setWagoStatusCode(303);
                            }
                        }else{
                            // logger.error("system exit alrm");
                            // System.exit(1);
                            logger.info("system unable to publish, reconnection attempted");
                            esbClient.stopEsbClient();
                            esbClient.startEsbClient();
                        }
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }else if(num_of_faults == 0 && taskIncomplete && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur") == "false" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "false" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode") == "true"){
                    this.taskIncomplete  = false;
                    this.miOccured = true;
                    this.dgrdmode = false;
                    this.mamode = false;
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("false","OK",
                            num_of_faults,fault_desc_list);
                    
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                        if (a == 0){
                            try{
                                persistenceManager.persistEntity(jcAlrmInfoDB);
                            }catch (Exception e) {
                                logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                                wago.setWagoStatusCode(303);
                            }
                        }else{
                            // logger.error("system exit alrm");
                            // System.exit(1);
                            logger.info("system unable to publish, reconnection attempted");
                            esbClient.stopEsbClient();
                            esbClient.startEsbClient();
                        }
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }else if(num_of_faults > 0 && plcdown && plcerrorlock){
                    this.plcdown = false;
                    this.plcerrorlock = false;
                    this.plcrecovered = true;
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("true","Critical",
                            num_of_faults,fault_desc_list);
                    
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                        if (a == 0){
                            try{
                                persistenceManager.persistEntity(jcAlrmInfoDB);
                            }catch (Exception e) {
                                logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                                wago.setWagoStatusCode(303);
                            }
                        }else{
                            // logger.error("system exit alrm");
                            // System.exit(1);
                            logger.info("system unable to publish, reconnection attempted");
                            esbClient.stopEsbClient();
                            esbClient.startEsbClient();
                        }
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }else if(num_of_faults == 0 && plcrecovered){
                    this.plcrecovered  = false;
                    this.plcerrorlock = true;
                    wago.setPlcLost("false");
                    jc_alrm_info jcAlrmInfoDB = wago.getJcAlrmInfo().generateJcAlrmInfo("false","OK",
                            num_of_faults,fault_desc_list);
                    sJsonReturnMsg = wago.getJcAlrmInfo().generateReturnMsg(jcAlrmInfoDB);
                    sReplyTransID = ConfigWago.getEsbJcAlrmInfo().toUpperCase();
                    logger.debug("JC_ALRM_INFO pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try{
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                        if (a == 0){
                            try{
                                persistenceManager.persistEntity(jcAlrmInfoDB);
                            }catch (Exception e) {
                                logger.error("AlarmPubThread - run : JC_ALRM_INFO Data Insert DB Failed", e);
                                wago.setWagoStatusCode(303);
                            }
                        }else{
                            // logger.error("system exit alrm");
                            // System.exit(1);
                            logger.info("system unable to publish, reconnection attempted");
                            esbClient.stopEsbClient();
                            esbClient.startEsbClient();
                          }
                    }catch (Exception e) {
                        logger.info("AlarmPubThread - run : ", e);
                    }
                }
                Thread.sleep(1 * 1000L);
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
                //     logger.error("Failed to sleep");
                //     e.printStackTrace();
                // }
            }
        } catch (ESBClientException e) {
            logger.error("EIUProcessStep1 - publishMessage Error : " + e.getMessage(), e);
        }
        // logger.debug("rc value: " + rc);
        return rc;
    }

}
