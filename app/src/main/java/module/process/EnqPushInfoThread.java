package module.process;

import com.google.gson.Gson;
import com.psa.gtosplus.esb.client.ESBClient;
import com.psa.gtosplus.esb.client.exception.ESBClientException;
import module.config.ConfigWago;
import module.entities.jc_alrm_info;
import module.entities.jc_enq_info;
import module.entities.jc_enq_info_r;
import module.entities.persistenceManager;
import module.message.JcAlrmInfo;
import module.message.JcEnqInfo;
import module.message.Payload;
import module.object.OPCUAWagoHM;
import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import module.Writenode.writeNode1;

import static module.Subscribenode.multipleSubscribe.handlerNode;

public class EnqPushInfoThread implements Runnable {
    private static Logger logger = Logger.getLogger(EnqPushInfoThread.class.getName());
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
    private List<String> pm_loop_list_enq = null;
    private List<String> uturn_loop_list_enq = null;
    private String error_c = "";
    private String error_txt = "";
    private String jc_status_c = "";
    private String alert_mode = "";
    private String mi_i = "";
    private String control_mode = "";
    private String barriers_down_i = "";
    private String traffic_light_i = "";
    private String mnl_xing_req_i = "";
    private String mnl_xing_req_i_check = "";

    // private Boolean stopPub = false;
    private boolean taskcomplete = false;
    private boolean mamiOccured = true;
    private boolean mamode = false;
    private boolean barriers_down = false;
    private boolean traffic_light = false;
    private boolean mnl_xing_req = false;

    public EnqPushInfoThread(persistenceManager persistenceManager, ESBClient esbClient,
            HashMap<String, Object> hmDbParam, OpcUaClient opcUaClient, Wago wago) {
        this.persistenceManager = persistenceManager;
        this.esbClient = esbClient;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        if (hmDbParam.size() > 0) {
            hmDbParam.clear();
        }
        // this.wago = new Wago();
        this.wago = wago;
        wago.setJcEnqInfo(new JcEnqInfo());
        gson = new Gson();
        String payloadX = "{\"gtosplus_ops_header\": " +
                "{\"source_m\":\"" + ConfigWago.getSourceM() + "\"," +
                "\"event_dt\":\"\"," +
                "\"event_id\":\"\"," +
                "\"trans_id\":\"JC_ENQ_INFO_R\"" +
                "}," +
                "\"gtosplus_ops_body\":{\"error_c\":\"" +
                "\",\"error_txt\":\"" +
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

    public EnqPushInfoThread(module.entities.persistenceManager persistenceManager, OpcUaClient opcUaClient,
            HashMap<String, Object> hmDbParam, Wago wago) {
        this.persistenceManager = persistenceManager;
        this.opcUaClient = opcUaClient;
        this.hmDbParam = hmDbParam;
        // this.wago = new Wago();
        this.wago = wago;
        wago.setJcEnqInfo(new JcEnqInfo());
        mnl_xing_req_i = wago.getJcEnqInfo().getMnl_xing_req_i();
        wago.getJcEnqInfo().setMnl_xing_req_i(mnl_xing_req_i);
        gson = new Gson();

        String payloadX = "{\"gtosplus_ops_header\": " +
                "{\"source_m\":\"" + ConfigWago.getSourceM() + "\"," +
                "\"event_dt\":\"\"," +
                "\"event_id\":\"\"," +
                "\"trans_id\":\"JC_ENQ_INFO_R\"" +
                "}," +
                "\"gtosplus_ops_body\":{\"error_c\":\"" +
                "\",\"error_txt\":\"" +
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

    public void start() {
        if (enqpushinfoThread == null) {
            enqpushinfoThread = new Thread(this, "alarmThread");
            enqpushinfoThread.start();
        }
    }

    public void stop() {
        if (enqpushinfoThread == null) {
            enqpushinfoThread = new Thread(this, "alarmThread");
            enqpushinfoThread.interrupt();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                error_c = "";
                error_txt = "";
                jc_status_c = "";
                alert_mode = "";
                mi_i = "";
                control_mode = "";
                barriers_down_i = "";
                traffic_light_i = "";
                mnl_xing_req_i = "";
                sJsonReturnMsg = "";
                sReplyTransID = "";
                num_of_faults = 0;
                fault_desc_list = null;
                num_of_pm = 0;
                pm_loop_list_enq = new ArrayList<String>();
                num_of_uturn = 0;
                uturn_loop_list_enq = new ArrayList<String>();

                // OPCUA Listening all equipment status
                handlerNode(opcUaClient, "acienqtags.properties");
                fault_desc_list = new ArrayList<>();
                for (String s : OPCUAWagoHM.OPCUAHm.keySet()) { 
                    if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Healthy")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 80404 + ",\"subcompo_m\": \"" + "Traffic_Light"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Traffic Light ) is having fault");
                            num_of_faults++;
                        }
                        // power supply
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_PSU_A_Health")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            fault_desc_list.add("{\"fault_code\": " + 70404 + ",\"subcompo_m\": \"" + "Power_Supply_A"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Power Supply A ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_PSU_B_Health")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            fault_desc_list.add("{\"fault_code\": " + 70404 + ",\"subcompo_m\": \"" + "Power_Supply_B"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Power Supply B ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_PSU_C_Health")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            fault_desc_list.add("{\"fault_code\": " + 70404 + ",\"subcompo_m\": \"" + "Power_Supply_C"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Power Supply C ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_PSU_D_Health")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            fault_desc_list.add("{\"fault_code\": " + 70404 + ",\"subcompo_m\": \"" + "Power_Supply_D"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Power Supply D ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_UPS_Health")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 70404 + ",\"subcompo_m\": \"" + "UPS"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( UPS ) is having fault");
                            num_of_faults++;
                        }
                        // barriers
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSO_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier01"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 01 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSC_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier01"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 01 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_DI_Hty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51404 + ",\"subcompo_m\": \"" + "Barrier01"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 01 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_002_ZSO_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier02"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_002_ZSC_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier02"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_002_DI_Hty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51404 + ",\"subcompo_m\": \"" + "Barrier02"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSO_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier03"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSC_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier03"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 02 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_DI_Hty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51404 + ",\"subcompo_m\": \"" + "Barrier03"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 03 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_004_ZSO_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier04"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_004_ZSC_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier04"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_004_DI_Hty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51404 + ",\"subcompo_m\": \"" + "Barrier04"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 04 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSO_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier05"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 05 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSC_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier05"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 05 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_DI_Hty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51404 + ",\"subcompo_m\": \"" + "Barrier05"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 05 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_006_ZSO_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier06"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_006_ZSC_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier06"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_006_DI_Hty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51404 + ",\"subcompo_m\": \"" + "Barrier06"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 06 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSO_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier07"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 07 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSC_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier07"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 07 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_DI_Hty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier07"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 07 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_008_ZSO_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51404 + ",\"subcompo_m\": \"" + "Barrier08"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s
                            .equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_008_ZSC_Faulty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier08"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_008_DI_Hty")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 51410 + ",\"subcompo_m\": \"" + "Barrier08"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier 08 ) is having fault");
                            num_of_faults++;
                        }
                        // line sensor
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Line_001_Detected")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 60405 + ",\"subcompo_m\": \"" + "Line_Sensor01"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Line_002_Detected")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 60405 + ",\"subcompo_m\": \"" + "Line_Sensor02"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 02 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Line_003_Detected")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 60405 + ",\"subcompo_m\": \"" + "Line_Sensor03"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 03 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Line_004_Detected")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 60405 + ",\"subcompo_m\": \"" + "Line_Sensor04"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 04 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Line_005_Detected")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 60405 + ",\"subcompo_m\": \"" + "Line_Sensor05"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 05 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Line_006_Detected")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 60405 + ",\"subcompo_m\": \"" + "Line_Sensor06"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 06 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Line_007_Detected")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 60405 + ",\"subcompo_m\": \"" + "Line_Sensor07"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 07 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Line_008_Detected")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 60405 + ",\"subcompo_m\": \"" + "Line_Sensor08"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Line Sensor 08 ) is having fault");
                            num_of_faults++;
                        }
                        // barrier loop
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Loop_001_Alarm")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 59404 + ",\"subcompo_m\": \"" + "Barrier_Loop01"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 01 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_Loop_002_Alarm")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 59404 + ",\"subcompo_m\": \"" + "Barrier_Loop02"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 02 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Loop_003_Alarm")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 59404 + ",\"subcompo_m\": \"" + "Barrier_Loop03"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 03 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_Loop_004_Alarm")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 59404 + ",\"subcompo_m\": \"" + "Barrier_Loop04"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 04 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Loop_005_Alarm")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 59404 + ",\"subcompo_m\": \"" + "Barrier_Loop05"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 05 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_Loop_006_Alarm")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 59404 + ",\"subcompo_m\": \"" + "Barrier_Loop06"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 06 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Loop_007_Alarm")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 59404 + ",\"subcompo_m\": \"" + "Barrier_Loop07"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 07 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_Loop_008_Alarm")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 59404 + ",\"subcompo_m\": \"" + "Barrier_Loop08"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Barrier Loop 08 ) is having fault");
                            num_of_faults++;
                        }
                        // lidar sensor
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_SW_Z1")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 40405 + ",\"subcompo_m\": \"" + "Lidar_Sensor01"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_SW_Z2")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 40405 + ",\"subcompo_m\": \"" + "Lidar_Sensor01"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001_Lidar_001_Alarm_1")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 41412 + ",\"subcompo_m\": \"" + "Lidar_Sensor01"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_002_SW_Z1")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 40405 + ",\"subcompo_m\": \"" + "Lidar_Sensor02"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_002_SW_Z2")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 40405 + ",\"subcompo_m\": \"" + "Lidar_Sensor02"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 02 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003_Lidar_002_Alarm_1")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            fault_desc_list.add("{\"fault_code\": " + 41412 + ",\"subcompo_m\": \"" + "Lidar_Sensor01"
                                    + "\",\"active_i\": " + "true" + "}");
                            logger.error(
                                    "pubCmdToWago - execJcUpd - OPCUA : Sub-component ( Lidar Sensor 01 ) is having fault");
                            num_of_faults++;
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_EWT_Loop")) {
                        // num_of_pm++;
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            pm_loop_list_enq.add("{\"pm_loop_m\": \"" + "Loop_1" + "\",\"active_i\": " + "true" + "}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            pm_loop_list_enq.add("{\"pm_loop_m\": \"" + "Loop_1" + "\",\"active_i\": " + "false" + "}");
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_NT_Loop")) {
                        // num_of_pm++;
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            pm_loop_list_enq.add("{\"pm_loop_m\": \"" + "Loop_3" + "\",\"active_i\": " + "true" + "}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            pm_loop_list_enq.add("{\"pm_loop_m\": \"" + "Loop_3" + "\",\"active_i\": " + "false" + "}");
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_ST_Loop")) {
                        // num_of_uturn++;
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            uturn_loop_list_enq
                                    .add("{\"uturn_loop_m\": \"" + "Loop_2" + "\",\"active_i\": " + "true" + "}");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            uturn_loop_list_enq
                                    .add("{\"uturn_loop_m\": \"" + "Loop_2" + "\",\"active_i\": " + "false" + "}");
                        }
                    // } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_4")) {
                    //     // num_of_uturn++;
                    //     if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                    //         uturn_loop_list_enq
                    //                 .add("{\"uturn_loop_m\": \"" + "Loop_4" + "\",\"active_i\": " + "true" + "}");
                    //     } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                    //         uturn_loop_list_enq
                    //                 .add("{\"uturn_loop_m\": \"" + "Loop_4" + "\",\"active_i\": " + "false" + "}");
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
                            wago.setCurJcSt("PM_Green");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            wago.setCurJcSt("PM_Green");
                        }
                    } else if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_PH_C")) {
                        if (OPCUAWagoHM.OPCUAHm.get(s) == "true") {
                            wago.getJcEnqInfo().setTraffic_light_i("true");
                        } else if (OPCUAWagoHM.OPCUAHm.get(s) == "false") {
                            wago.getJcEnqInfo().setTraffic_light_i("false");
                        }
                    } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_002_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_004_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_006_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSC") == "true"
                                && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_008_ZSC") == "true") {
                            wago.getJcEnqInfo().setBarriers_down_i("true");
                    } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_002_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_003_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001B_BARR_004_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_005_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003A_BARR_006_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_007_ZSC") == "false"
                                || OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APJ24_LN_003B_BARR_008_ZSC") == "false") {
                            wago.getJcEnqInfo().setBarriers_down_i("false");
                        } 
                        mnl_xing_req_i = wago.getJcEnqInfo().getMnl_xing_req_i();
                        wago.getJcEnqInfo().setMnl_xing_req_i(mnl_xing_req_i);
                        mnl_xing_req_i_check = wago.getJcEnqInfo().getMnl_xing_req_i();
                }

                // try {
                //     persistenceManager.updatePLCHB(Integer.parseInt(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PLC_Hearbeat")));
                //     // logger.debug("PLC HB: "+OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PLC_Hearbeat"));
                // }catch(NumberFormatException ne){
    
                // }
                // catch (Exception e) {
                //     logger.error("HBPublishThread - run : PLC heartbeat lost", e);
                // }

                num_of_pm = pm_loop_list_enq.size();
                num_of_uturn = uturn_loop_list_enq.size();
                if (num_of_faults > 0 && mamiOccured
                        && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "true") {
                    this.taskcomplete = true;
                    this.mamiOccured = false;
                    this.mamode = true;

                    try {
                        writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL",
                                false);
                        logger.info("writenode1");
                    } catch (Exception e) {
                        logger.error("call : OPCUA write node failed:", e);
                    }
                    jc_enq_info_r jcEnqInfoRDB = wago.getJcEnqInfo().generateJcEnqInfoR("Failed", "", wago.getCurJcSt(),
                            "E-Critical", "true", wago.getControlMode(), wago.getJcEnqInfo().getBarriers_down_i(), wago.getJcEnqInfo().getTraffic_light_i(), wago.getJcEnqInfo().getMnl_xing_req_i(), num_of_pm, pm_loop_list_enq, num_of_uturn,
                            uturn_loop_list_enq, num_of_faults, fault_desc_list);
                    
                    sJsonReturnMsg = wago.getJcEnqInfo().generateFaultMsg(jcEnqInfoRDB);
                    sReplyTransID = ConfigWago.getEsbJcEnqInfoR().toUpperCase();
                    logger.debug("JC_ENQ_INFO_R pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try {
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                    if (a == 0){
                        try {
                            persistenceManager.persistEntity(jcEnqInfoRDB);
                        } catch (Exception e) {
                            logger.error("EnqPushInfoThread - run : JC_ENQ_INFO_R Data Insert DB Failed", e);
                            wago.setWagoStatusCode(303);
                        }
                    }else{
                        // logger.error("system exit enq");
                        // System.exit(1);
                        logger.info("system unable to publish, reconnection attempted");
                        esbClient.stopEsbClient();
                        esbClient.startEsbClient();
                    }
                    } catch (Exception e) {
                        logger.info("EnqPushInfoThread - run : ", e);
                    }
                 } else if (num_of_faults == 0 && mamiOccured
                        && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "true") {
                    this.taskcomplete = true;
                    this.mamiOccured = false;
                    this.mamode = true;

                    try {
                        writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL",
                                false);
                        logger.info("writenode1");
                    } catch (Exception e) {
                        logger.error("call : OPCUA write node failed:", e);
                    }
                    jc_enq_info_r jcEnqInfoRDB = wago.getJcEnqInfo().generateJcEnqInfoR("OK", "", wago.getCurJcSt(),
                            "OK", "false", wago.getControlMode(), wago.getJcEnqInfo().getBarriers_down_i(), wago.getJcEnqInfo().getTraffic_light_i(), wago.getJcEnqInfo().getMnl_xing_req_i(), num_of_pm, pm_loop_list_enq, num_of_uturn,
                            uturn_loop_list_enq, num_of_faults, fault_desc_list);
                   
                    sJsonReturnMsg = wago.getJcEnqInfo().generateFaultMsg(jcEnqInfoRDB);
                    sReplyTransID = ConfigWago.getEsbJcEnqInfoR().toUpperCase();
                    logger.debug("JC_ENQ_INFO_R pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try {
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                    if (a == 0){
                        try {
                            persistenceManager.persistEntity(jcEnqInfoRDB);
                        } catch (Exception e) {
                            logger.error("EnqPushInfoThread - run : JC_ENQ_INFO_R Data Insert DB Failed", e);
                            wago.setWagoStatusCode(303);
                        }
                    }else{
                        // logger.error("system exit enq");
                        // System.exit(1);
                        logger.info("system unable to publish, reconnection attempted");
                        esbClient.stopEsbClient();
                        esbClient.startEsbClient();
                    }
                    } catch (Exception e) {
                        logger.info("EnqPushInfoThread - run : ", e);
                    }
                }else if (num_of_faults >= 0 && mamode
                         && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "true"
                         && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode") == "true"
                         && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur") == "false"
                         ) {
                     this.taskcomplete = true;
                     this.mamiOccured = true;
                     this.mamode = false;

                     jc_enq_info_r jcEnqInfoRDB = wago.getJcEnqInfo().generateJcEnqInfoR("Degraded", "",
                             wago.getCurJcSt(),
                             "Warning", "false", wago.getControlMode(), wago.getJcEnqInfo().getBarriers_down_i(), wago.getJcEnqInfo().getTraffic_light_i(), wago.getJcEnqInfo().getMnl_xing_req_i(), num_of_pm, pm_loop_list_enq, num_of_uturn,
                             uturn_loop_list_enq, num_of_faults, fault_desc_list);
                     
                     sJsonReturnMsg = wago.getJcEnqInfo().generateReturnMsg(jcEnqInfoRDB);
                     sReplyTransID = ConfigWago.getEsbJcEnqInfoR().toUpperCase();
                     logger.debug("JC_ENQ_INFO_R pub message: \n" + sJsonReturnMsg);
                     // Publish out to PSA sollace
                     try {
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                    if (a == 0){
                        try {
                            persistenceManager.persistEntity(jcEnqInfoRDB);
                        } catch (Exception e) {
                            logger.error("EnqPushInfoThread - run : JC_ENQ_INFO_R Data Insert DB Failed", e);
                            wago.setWagoStatusCode(303);
                        }
                    }else{
                        // logger.error("system exit enq");
                        // System.exit(1);
                        logger.info("system unable to publish, reconnection attempted");
                        esbClient.stopEsbClient();
                        esbClient.startEsbClient();
                    }
                    } catch (Exception e) {
                        logger.info("EnqPushInfoThread - run : ", e);
                    }
                } else if (num_of_faults == 0 && taskcomplete
                        && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "false"
                        && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode") == "true"
                        && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur") == "false"
                        ) {
                    this.taskcomplete = false;
                    this.mamiOccured = true;
                    this.mamode = false;

                    jc_enq_info_r jcEnqInfoRDB = wago.getJcEnqInfo().generateJcEnqInfoR("OK", "", wago.getCurJcSt(),
                            "OK", "false", wago.getControlMode(), wago.getJcEnqInfo().getBarriers_down_i(), wago.getJcEnqInfo().getTraffic_light_i(), wago.getJcEnqInfo().getMnl_xing_req_i(), num_of_pm, pm_loop_list_enq, num_of_uturn,
                            uturn_loop_list_enq, num_of_faults, fault_desc_list);
                    
                    sJsonReturnMsg = wago.getJcEnqInfo().generateReturnMsg(jcEnqInfoRDB);
                    sReplyTransID = ConfigWago.getEsbJcEnqInfoR().toUpperCase();
                    logger.debug("JC_ENQ_INFO_R pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try {
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                    if (a == 0){
                        try {
                            persistenceManager.persistEntity(jcEnqInfoRDB);
                        } catch (Exception e) {
                            logger.error("EnqPushInfoThread - run : JC_ENQ_INFO_R Data Insert DB Failed", e);
                            wago.setWagoStatusCode(303);
                        }
                    }else{
                        // logger.error("system exit enq");
                        // System.exit(1);
                        logger.info("system unable to publish, reconnection attempted");
                        esbClient.stopEsbClient();
                        esbClient.startEsbClient();
                    }
                    } catch (Exception e) {
                        logger.info("EnqPushInfoThread - run : ", e);
                    }
                } else if (num_of_faults > 0 && taskcomplete
                        && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.APMS_DGRD_MODE") == "false"
                        && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode") == "true"
                        && OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Report_MI_Occur") == "true"
                        ) {
                    this.taskcomplete = false;
                    this.mamiOccured = true;
                    this.mamode = false;

                    jc_enq_info_r jcEnqInfoRDB = wago.getJcEnqInfo().generateJcEnqInfoR("Failed", "", wago.getCurJcSt(),
                            "E-Critical", "true", wago.getControlMode(), wago.getJcEnqInfo().getBarriers_down_i(), wago.getJcEnqInfo().getTraffic_light_i(), wago.getJcEnqInfo().getMnl_xing_req_i(), num_of_pm, pm_loop_list_enq, num_of_uturn,
                            uturn_loop_list_enq, num_of_faults, fault_desc_list);
                    // try {
                    //     persistenceManager.persistEntity(jcEnqInfoRDB);
                    // } catch (Exception e) {
                    //     logger.error("EnqPushInfoThread - run : JC_ENQ_INFO_R Data Insert DB Failed", e);
                    //     wago.setWagoStatusCode(303);
                    // }
                    sJsonReturnMsg = wago.getJcEnqInfo().generateReturnMsg(jcEnqInfoRDB);
                    sReplyTransID = ConfigWago.getEsbJcEnqInfoR().toUpperCase();
                    logger.debug("JC_ENQ_INFO_R pub message: \n" + sJsonReturnMsg);
                    // Publish out to PSA sollace
                    try {
                        int a = publishMessage(sReplyTransID, sJsonReturnMsg);
                    if (a == 0){
                        try {
                            persistenceManager.persistEntity(jcEnqInfoRDB);
                        } catch (Exception e) {
                            logger.error("EnqPushInfoThread - run : JC_ENQ_INFO_R Data Insert DB Failed", e);
                            wago.setWagoStatusCode(303);
                        }
                    }else{
                        // logger.error("system exit enq");
                        // System.exit(1);
                        logger.info("system unable to publish, reconnection attempted");
                        esbClient.stopEsbClient();
                        esbClient.startEsbClient();
                    }
                    } catch (Exception e) {
                        logger.info("EnqPushInfoThread - run : ", e);
                    }
                }
                Thread.sleep(1 * 1000L); // only for debug purpose
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        return rc;
    }
}
