package module.process;

import com.google.gson.Gson;
import com.psa.gtosplus.esb.client.ESBClient;
import com.psa.gtosplus.esb.client.exception.ESBClientException;
import module.config.ConfigWago;
import module.entities.jc_enq_info_r;
import module.entities.persistenceManager;
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

public class EnqPushInfoCallable implements Callable<Void> {
    private static Logger logger = Logger.getLogger(EnqPushInfoCallable.class.getName());
    private module.entities.persistenceManager persistenceManager = null;
    private Thread enqpushinfoThread = null;
    public static boolean EIU1Alive = true;
    private ESBClient esbClient = null;
    private JcEnqInfo jcEnqInfoM = null;
    private int num_of_faults = 0;
    private List<String> fault_desc_list = null;
    private Wago wago = null;
    private String sJsonReturnMsg = "";
    private String sReplyTransID = "";
    private Payload payload = null;
    private Gson gson = null;
    private OpcUaClient opcUaClient = null;
    private HashMap<String, Object> hmDbParam = null;
    private int num_of_pm = 0;
    private int num_of_uturn = 0;
    private List<String> pm_loop_list = null;
    private List<String> uturn_loop_list = null;
    private String error_c = "";
    private String error_txt="";
    private String jc_status_c="";
    private String alert_mode="";
    private String mi_i="";
    private String control_mode="";
    private String barriers_down_i = "";
    private String traffic_light_i = "";
    private String mnl_xing_req_i = "";
    private String mnl_xing_req_i_check = "";

    public EnqPushInfoCallable(persistenceManager persistenceManager, ESBClient esbClient, HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient, String mnl_xing_req_i) {
        this.persistenceManager = persistenceManager;
        this.esbClient = esbClient;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        if (hmDbParam.size()>0){hmDbParam.clear();}
        this.wago = new Wago();
        wago.setJcEnqInfo(new JcEnqInfo());
        this.mnl_xing_req_i = mnl_xing_req_i;
        wago.getJcEnqInfo().setMnl_xing_req_i(mnl_xing_req_i);
        logger.info(String.format("EnqPushInfoCallable - Constructor - mnl_xing_req_i: %s", mnl_xing_req_i));
        mnl_xing_req_i_check = wago.getJcEnqInfo().getMnl_xing_req_i();
        logger.info(String.format("EnqPushInfoCallable - Constructor - mnl_xing_req_i_check: %s", mnl_xing_req_i_check));
        gson = new Gson();
        String payloadX = "{\"gtosplus_ops_header\": "+
                "{\"source_m\":\""+ ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_ENQ_INFO_R\""+
                "},"+
                "\"gtosplus_ops_body\":{\"error_c\":\"OK" +
                "\",\"error_txt\":\""+
                "\",\"jc_id\":" + ConfigWago.getJcId() +
                ",\"jc_m\":\"" + ConfigWago.getJcM() +
                "\",\"jc_status_c\":\"" +
                "\",\"alert_mode\":\"" +
                "\",\"mi_i\":false" +
                ",\"control_mode\":\"" +
                "\",\"barriers_down_i\":false" +
                ",\"traffic_light_i\":false" +
                ",\"mnl_xing_req_i\":false" +
                ",\"num_of_pm_loop\":0" +
                ",\"pm_loop_list\":[{}]" +
                ",\"num_of_uturn_loop\":0" +
                ",\"uturn_loop_list\":[{}]" +
                ",\"num_of_faults\":0" +
                ",\"fault_code_desc_list\":[{}]}}";
        try {
            payload = gson.fromJson(payloadX, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("EnqPushInfoThread - Constructor - payloadX: %s", payloadX), e);
            return;
        }
        jcEnqInfoM = payload.generatePayloadJcEnqInfo();
        wago.setJcEnqInfo(jcEnqInfoM);
    }

    public EnqPushInfoCallable(module.entities.persistenceManager persistenceManager, OpcUaClient opcUaClient, HashMap<String, Object> hmDbParam, Wago wago) {
        this.persistenceManager = persistenceManager;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        this.wago = wago;
        wago.setJcEnqInfo(new JcEnqInfo());
        // this.wago = new Wago();
        gson = new Gson();

        String payloadX = "{\"gtosplus_ops_header\": "+
                "{\"source_m\":\""+ ConfigWago.getSourceM()+"\","+
                "\"event_dt\":\"\","+
                "\"event_id\":\"\","+
                "\"trans_id\":\"JC_ENQ_INFO_R\""+
                "},"+
                "\"gtosplus_ops_body\":{\"error_c\":\"OK" +
                "\",\"error_txt\":\""+
                "\",\"jc_id\":" + ConfigWago.getJcId() +
                ",\"jc_m\":\"" + ConfigWago.getJcM() +
                "\",\"jc_status_c\":\"" +
                "\",\"alert_mode\":\"" +
                "\",\"mi_i\":false" +
                ",\"control_mode\":\"" +
                "\",\"barriers_down_i\":false" +
                ",\"traffic_light_i\":false" +
                ",\"mnl_xing_req_i\":false" +
                ",\"num_of_pm_loop\":0" +
                ",\"pm_loop_list\":[{}]" +
                ",\"num_of_uturn_loop\":0" +
                ",\"uturn_loop_list\":[{}]" +
                ",\"num_of_faults\":0" +
                ",\"fault_code_desc_list\":[{}]}}";
        try {
            payload = gson.fromJson(payloadX, Payload.class);
        } catch (Exception e) {
            logger.info(String.format("EnqPushInfoThread - Constructor - payloadX: %s", payloadX), e);
            return;
        }
        jcEnqInfoM = payload.generatePayloadJcEnqInfo();
        wago.setJcEnqInfo(jcEnqInfoM);
    }

    @Override
    public Void call() throws Exception  {
        // while (true) {
            try {
                error_c = "";
                error_txt = "";
                jc_status_c = "";
                alert_mode = "";
                mi_i = "";
                control_mode = "";
                barriers_down_i = "";
                traffic_light_i = "";
                //mnl_xing_req_i = "";
                sJsonReturnMsg = "";
                sReplyTransID = "";
                num_of_faults = 0;
                fault_desc_list = new ArrayList<String>();
                num_of_pm = 0;
                pm_loop_list = new ArrayList<String>();
                num_of_uturn = 0;
                uturn_loop_list = new ArrayList<String>();

                //OPCUA Listening all equipment status
                handlerNode(opcUaClient, "acienqtags.properties");
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
                            fault_desc_list.add("{\"fault_code\": "+51411+",\"subcompo_m\": \""+"Barrier01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 01 ) cannot open");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+51410+",\"subcompo_m\": \""+"Barrier01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 01 ) cannot close");
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
                            fault_desc_list.add("{\"fault_code\": " +52411+ ",\"subcompo_m\": \"" + "Barrier02" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) cannot open");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_002_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +52410+ ",\"subcompo_m\": \"" + "Barrier02" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) cannot close");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_002_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +52404+ ",\"subcompo_m\": \"" + "Barrier02" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +53411+ ",\"subcompo_m\": \"" + "Barrier03" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 03 ) cannot open");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +53410+ ",\"subcompo_m\": \"" + "Barrier03" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 03 ) cannot close");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +53404+ ",\"subcompo_m\": \"" + "Barrier03" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 03 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_004_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +54411+ ",\"subcompo_m\": \"" + "Barrier04" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) cannot open");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_004_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +54410+ ",\"subcompo_m\": \"" + "Barrier04" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) cannot close");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_004_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +54404+ ",\"subcompo_m\": \"" + "Barrier04" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +55411+ ",\"subcompo_m\": \"" + "Barrier05" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 05 ) cannot open");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +55410+ ",\"subcompo_m\": \"" + "Barrier05" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 05 ) cannot close");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +55404+ ",\"subcompo_m\": \"" + "Barrier05" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 05 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_006_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +56411+ ",\"subcompo_m\": \"" + "Barrier06" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) cannot open");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_006_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +56410+ ",\"subcompo_m\": \"" + "Barrier06" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) cannot close");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_006_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +56404+ ",\"subcompo_m\": \"" + "Barrier06" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +57411+ ",\"subcompo_m\": \"" + "Barrier07" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 07 ) cannot open");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +57410+ ",\"subcompo_m\": \"" + "Barrier07" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 07 ) cannot close");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +57404+ ",\"subcompo_m\": \"" + "Barrier07" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 07 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_008_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +58411+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) cannot open");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_008_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +58410+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) cannot close");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_008_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +58404+ ",\"subcompo_m\": \"" + "Barrier08" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_009_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +59411+ ",\"subcompo_m\": \"" + "Barrier09" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 09 ) cannot open");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_009_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +59410+ ",\"subcompo_m\": \"" + "Barrier09" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 09 ) cannot close");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_009_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +59404+ ",\"subcompo_m\": \"" + "Barrier09" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 09 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_010_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +510411+ ",\"subcompo_m\": \"" + "Barrier10" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 10 ) cannot open");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_010_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +510410+ ",\"subcompo_m\": \"" + "Barrier10" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 10 ) cannot close");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_010_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +510404+ ",\"subcompo_m\": \"" + "Barrier10" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 10 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_011_ZSO_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +51411+ ",\"subcompo_m\": \"" + "Barrier11" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 11 ) cannot open");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_011_ZSC_Faulty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +511410+ ",\"subcompo_m\": \"" + "Barrier11" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 11 ) cannot close");
                            num_of_faults++;
                        }
                        
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_011_DI_Hty")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": " +511404+ ",\"subcompo_m\": \"" + "Barrier11" + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 11 ) is having fault");
                            num_of_faults++;
                        }
                        
                    }
                    //line sensor
                    else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Line_001_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+61405+",\"subcompo_m\": \""+"Line_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 01 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Line_002_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+62405+",\"subcompo_m\": \""+"Line_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 02 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Line_003_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+63405+",\"subcompo_m\": \""+"Line_Sensor03"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 03 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Line_004_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+64405+",\"subcompo_m\": \""+"Line_Sensor04"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 04 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Line_005_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+65405+",\"subcompo_m\": \""+"Line_Sensor05"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 05 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Line_006_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+66405+",\"subcompo_m\": \""+"Line_Sensor06"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 06 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Line_007_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+67405+",\"subcompo_m\": \""+"Line_Sensor07"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 07 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Line_008_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+68405+",\"subcompo_m\": \""+"Line_Sensor08"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 08 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_Line_009_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+69405+",\"subcompo_m\": \""+"Line_Sensor09"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 09 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_Line_010_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+610405+",\"subcompo_m\": \""+"Line_Sensor10"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 10 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_Line_011_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+611405+",\"subcompo_m\": \""+"Line_Sensor11"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 11 ) detected obstacles");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_Line_012_Detected")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+612405+",\"subcompo_m\": \""+"Line_Sensor12"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 12 ) detected obstacles");
                            num_of_faults++;
                        }
                    }
                     //barrier loop
                     else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Loop_001_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+31404+",\"subcompo_m\": \""+"Barrier_Loop01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 01 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Loop_002_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+32404+",\"subcompo_m\": \""+"Barrier_Loop02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 02 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Loop_003_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+33404+",\"subcompo_m\": \""+"Barrier_Loop03"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 03 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003AB_Loop_004_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+34404+",\"subcompo_m\": \""+"Barrier_Loop04"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 04 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Loop_005_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+35404+",\"subcompo_m\": \""+"Barrier_Loop05"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 05 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Loop_006_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+36404+",\"subcompo_m\": \""+"Barrier_Loop06"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 06 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Loop_007_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+37404+",\"subcompo_m\": \""+"Barrier_Loop07"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 07 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Loop_008_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+38404+",\"subcompo_m\": \""+"Barrier_Loop08"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 08 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_Loop_009_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+39404+",\"subcompo_m\": \""+"Barrier_Loop09"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 09 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_Loop_010_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+310404+",\"subcompo_m\": \""+"Barrier_Loop10"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 10 ) is having fault");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_Loop_011_Alarm")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+311404+",\"subcompo_m\": \""+"Barrier_Loop11"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 11 ) is having fault");
                            num_of_faults++;
                        }
                    }
                    //lidar sensor
                    else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_SW_Z1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+41405+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) zone detected");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_SW_Z2")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+41405+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) line detected");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_Alarm_1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+41410+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) contamination");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_Active")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+41412+",\"subcompo_m\": \""+"Lidar_Sensor01"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) power down");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_002_SW_Z1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+42405+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) zone detected");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_002_SW_Z2")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+42405+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) line detected");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_002_Alarm_1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+42410+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) contamination");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_002_Active")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+42412+",\"subcompo_m\": \""+"Lidar_Sensor02"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) power down");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_003_SW_Z1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+43405+",\"subcompo_m\": \""+"Lidar_Sensor03"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 03 ) zone detected");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_003_SW_Z2")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+43405+",\"subcompo_m\": \""+"Lidar_Sensor03"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 03 ) line detected");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_003_Alarm_1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+43410+",\"subcompo_m\": \""+"Lidar_Sensor03"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 03 ) contamination");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_003_Active")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+43412+",\"subcompo_m\": \""+"Lidar_Sensor03"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 03 ) power down");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005_Lidar_004_SW_Z1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+44405+",\"subcompo_m\": \""+"Lidar_Sensor04"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 04 ) zone detected");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005_Lidar_004_SW_Z2")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+44405+",\"subcompo_m\": \""+"Lidar_Sensor04"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 04 ) line detected");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005_Lidar_004_Alarm_1")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+44410+",\"subcompo_m\": \""+"Lidar_Sensor04"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 04 ) contamination");
                            num_of_faults++;
                        }
                    }else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005_Lidar_004_Active")) {
                        if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
                            fault_desc_list.add("{\"fault_code\": "+44412+",\"subcompo_m\": \""+"Lidar_Sensor04"+"\",\"active_i\": "+"true"+"}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 04 ) power down");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_EWT_Loop")) {
                        num_of_pm++;
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            pm_loop_list.add("{\"pm_loop_m\": \"" + "Loop_1" + "\",\"active_i\": " + "true" + "}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            pm_loop_list.add("{\"pm_loop_m\": \"" + "Loop_1" + "\",\"active_i\": " + "false" + "}");
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_NT_Loop")) {
                        num_of_pm++;
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            pm_loop_list.add("{\"pm_loop_m\": \"" + "Loop_3" + "\",\"active_i\": " + "true" + "}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            pm_loop_list.add("{\"pm_loop_m\": \"" + "Loop_3" + "\",\"active_i\": " + "false" + "}");
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_ST_Loop")) {
                        num_of_uturn++;
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            uturn_loop_list.add("{\"uturn_loop_m\": \"" + "Loop_2" + "\",\"active_i\": " + "true" + "}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            uturn_loop_list.add("{\"uturn_loop_m\": \"" + "Loop_2" + "\",\"active_i\": " + "false" + "}");
                        }
                    // } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_4")) {
                    //     num_of_uturn++;
                    //     if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                    //         uturn_loop_list.add("{\"uturn_loop_m\": \"" + "Loop_4" + "\",\"active_i\": " + "true" + "}");
                    //     } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                    //         uturn_loop_list.add("{\"uturn_loop_m\": \"" + "Loop_4" + "\",\"active_i\": " + "false" + "}");
                    //     }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            wago.setControlMode("Remote");
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            wago.setControlMode("Local");
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Repair_Mode")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            wago.setControlMode("Local");
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            wago.setCurJcSt("PM_Red");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            wago.setCurJcSt("PM_Green");
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_C")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true" && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.System_Control_Mode") == "true") {
                            wago.getJcEnqInfo().setTraffic_light_i("true");
                        } else {
                            wago.getJcEnqInfo().setTraffic_light_i("false");
                        }
                    } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_002_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_004_ZSC") == "false"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSC") == "false"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_006_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_008_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_009_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_010_ZSC") == "false"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_011_ZSC") == "false") {
                            wago.getJcEnqInfo().setBarriers_down_i("true");
                    } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_002_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_004_ZSC") == "true"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSC") == "true"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_006_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_008_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005A_BARR_009_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_010_ZSC") == "true"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_005B_BARR_011_ZSC") == "true") {
                            wago.getJcEnqInfo().setBarriers_down_i("false");
                        }
                        
                }

                logger.info(String.format("EnqPushInfoCallable - Outside For Loop - mnl_xing_req_i: %s", mnl_xing_req_i));
                mnl_xing_req_i_check = wago.getJcEnqInfo().getMnl_xing_req_i();
                logger.info(String.format("EnqPushInfoCallable - Outside - mnl_xing_req_i: %s", mnl_xing_req_i_check));

                if (num_of_faults > 0) {
                    jc_enq_info_r jcEnqInfoRDB = wago.getJcEnqInfo().generateJcEnqInfoR("Failed", "", wago.getCurJcSt(),
                            "Critical", "true", wago.getControlMode(), wago.getJcEnqInfo().getBarriers_down_i(), wago.getJcEnqInfo().getTraffic_light_i(), mnl_xing_req_i, num_of_pm, pm_loop_list, num_of_uturn,
                            uturn_loop_list, num_of_faults, fault_desc_list);
                    try {
                        persistenceManager.persistEntity(jcEnqInfoRDB);
                    } catch (Exception e) {
                        logger.error("EnqPushInfoThread - run : JC_ENQ_INFO_R Data Insert DB Failed", e);
                        wago.setWagoStatusCode(303);
                    }
                    sJsonReturnMsg = wago.getJcEnqInfo().generateFaultMsg(jcEnqInfoRDB);
                    sReplyTransID = ConfigWago.getEsbJcEnqInfoR().toUpperCase();
                    logger.debug("JC_ENQ_INFO_R pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try {
                        publishMessage(sReplyTransID, sJsonReturnMsg);
                    } catch (Exception e) {
                        logger.info("EnqPushInfoThread - run : ", e);
                    }
                } 
                else {
                    jc_enq_info_r jcEnqInfoRDB = wago.getJcEnqInfo().generateJcEnqInfoR("OK", "", wago.getCurJcSt(),
                            "OK", "false", wago.getControlMode(), wago.getJcEnqInfo().getBarriers_down_i(), wago.getJcEnqInfo().getTraffic_light_i(), mnl_xing_req_i, num_of_pm, pm_loop_list, num_of_uturn,
                            uturn_loop_list, num_of_faults, fault_desc_list);
                    try {
                        persistenceManager.persistEntity(jcEnqInfoRDB);
                    } catch (Exception e) {
                        logger.error("EnqPushInfoThread - run : JC_ENQ_INFO_R Data Insert DB Failed", e);
                        wago.setWagoStatusCode(303);
                    }
                    sJsonReturnMsg = wago.getJcEnqInfo().generateReturnMsg(jcEnqInfoRDB);
                    sReplyTransID = ConfigWago.getEsbJcEnqInfoR().toUpperCase();
                    logger.debug("JC_ENQ_INFO_R pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try {
                        publishMessage(sReplyTransID, sJsonReturnMsg);
                    } catch (Exception e) {
                        logger.info("EnqPushInfoThread - run : ", e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
