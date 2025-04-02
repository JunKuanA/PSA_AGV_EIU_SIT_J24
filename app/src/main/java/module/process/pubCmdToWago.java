// package module.process;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;

// import module.Writenode.writeNode1;
// import module.object.OPCUAWagoHM;
// import org.apache.log4j.Logger;

// import com.google.gson.Gson;
// import com.psa.gtosplus.esb.client.ESBClient;
// import com.psa.gtosplus.esb.client.exception.ESBClientException;

// import module.config.ConfigWago;
// import module.entities.jc_ack_r;
// import module.entities.jc_enq_info_r;
// import module.entities.jc_rst_alrm_r;
// import module.entities.jc_upd_st_r;
// import module.entities.persistenceManager;
// import module.entities.status_code_list;
// import module.message.JcAckR;
// import module.message.Payload;
// import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

// import static module.Subscribenode.multipleSubscribe.handlerNode;


// public class pubCmdToWago implements Runnable {
//     private static Logger logger = Logger.getLogger(pubCmdToWago.class.getName());

//     private Thread processMsgToWagoThread = null;
//     private ESBClient esbClient = null;
//     private persistenceManager dbCtrl = null;
//     private int threadId = 0;
//     private String threadName = "";
//     private Wago wago = null;
//     private HashMap<String, Object> hmDbParam = null;
//     private boolean taskCompleted = false;
//     private String sJsonReturnMsg = "";
//     private String sReplyTransID = "";
//     private Payload payload = null;

//     private JcAckR jcAckRM = null;
//     private OpcUaClient opcUaClient = null;



//     public pubCmdToWago(String threadName, Wago wago, persistenceManager dbCtrl,HashMap<String, Object> hmDbParam,OpcUaClient opcUaClient) {
//         this.threadName = threadName;
//         this.esbClient = wago.getEsbClient();
//         this.wago = wago;
//         this.dbCtrl = dbCtrl;
//         this.hmDbParam = hmDbParam;
//         this.opcUaClient = opcUaClient;
//         if (hmDbParam.size()>0){hmDbParam.clear();}
//     }
//     public pubCmdToWago(String threadName, Wago wago, persistenceManager dbCtrl,HashMap<String, Object> hmDbParam) {
//         this.threadName = threadName;
//         this.esbClient = wago.getEsbClient();
//         this.wago = wago;
//         this.dbCtrl = dbCtrl;
//         this.hmDbParam = hmDbParam;
//         if (hmDbParam.size()>0){hmDbParam.clear();}
//     }

//     public pubCmdToWago(String threadName, Wago wago, persistenceManager dbCtrl, int threadId,OpcUaClient opcUaClient) {
//         this.threadName = threadName;
//         this.esbClient = wago.getEsbClient();
//         this.wago = wago;
//         this.dbCtrl = dbCtrl;
//         this.threadId = threadId;
//         this.opcUaClient = opcUaClient;
//     }

//     public void start() {
//         if (processMsgToWagoThread == null) {
//             processMsgToWagoThread = new Thread(this, threadName);
//             processMsgToWagoThread.start();
//         }
//     }

//     @Override
//     public void run() {
//         // TODO Auto-generated method stub
//         if (wago.getCmdType().equalsIgnoreCase("JC_ALRM_RST")){
//             execJcRst(wago);
//         } else if (wago.getCmdType().equalsIgnoreCase("JC_UPD_ST")){
//             execJcUpd();
//         // } else if (wago.getCmdType().equalsIgnoreCase("JC_ENQ_INFO")){
//         //     execJcEnqInfo();
//         } else {
//             logger.error("pubCmdToWago - Cmd type set wrongly" + wago.getCmdType());
//             Thread.currentThread().interrupt();
//         }
//     }

//     private void execJcEnqInfo() {
// //        this.taskCompleted = false;
// //        payload = null;
// //        sJsonReturnMsg = "";
// //        sReplyTransID = "";
// //        int numOfFault = 0;
// //        List<String> fault_code_desc = new ArrayList<String>();
// //        int numOfPm = 0;
// //        List<String> pm_loop_list = new ArrayList<String>();
// //        int numOfUturn = 0;
// //        List<String> uturn_loop_list = new ArrayList<String>();
// //        String jc_status_c ="";
// //        String alert_mode ="";
// //        String control_mode ="";
// //        String mi_i = "";
// //        Gson gson = new Gson();
// //
// //        try{
// //            //OPCUA listening here
// //            try{
// //                handlerNode(opcUaClient,"./acienqtags.properties");
// //                if(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL") == "true"){
// //                    jc_status_c = "PM_Red";
// //                }else{
// //                    jc_status_c = "PM_Green";
// //                }
// //                alert_mode ="";/* e-critical, critical, warning, ok */
// //
// //                if(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode") == "true"){
// //                    control_mode ="Remote";
// //                } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Maintenance_Mode") == "true"){
// //                    control_mode ="Local";
// //                } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Repair_Mode") == "true"){
// //                    control_mode ="Local";
// //                }
// //
// //                numOfPm = 2;
// //                if(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_1") == "true"){
// //                    pm_loop_list.add("{\"pm_loop_m\": \""+"Loop_1"+"\",\"active_i\": "+"true"+"}");
// //                } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_1") == "false"){
// //                    pm_loop_list.add("{\"pm_loop_m\": \""+"Loop_1"+"\",\"active_i\": "+"false"+"}");
// //                }
// //
// //                if(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_3") == "true"){
// //                    pm_loop_list.add("{\"pm_loop_m\": \""+"Loop_3"+"\",\"active_i\": "+"true"+"}");
// //                } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_3") == "false"){
// //                    pm_loop_list.add("{\"pm_loop_m\": \""+"Loop_3"+"\",\"active_i\": "+"false"+"}");
// //                }
// //
// //                numOfUturn = 2;
// //                if(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_2") == "true"){
// //                    uturn_loop_list.add("{\"pm_loop_m\": \""+"Loop_2"+"\",\"active_i\": "+"true"+"}");
// //                } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_2") == "false"){
// //                    uturn_loop_list.add("{\"pm_loop_m\": \""+"Loop_2"+"\",\"active_i\": "+"false"+"}");
// //                }
// //
// //                if(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_4") == "true"){
// //                    uturn_loop_list.add("{\"pm_loop_m\": \""+"Loop_4"+"\",\"active_i\": "+"true"+"}");
// //                } else if (OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.TL_Loop_4") == "false"){
// //                    uturn_loop_list.add("{\"pm_loop_m\": \""+"Loop_4"+"\",\"active_i\": "+"false"+"}");
// //                }
// //
// //
// //                //uturn_loop_list.add("{\"pm_loop_m\": \""+"pm loop name"+"\",\"active_i\": "+"true/false"+"}");
// //                numOfUturn = uturn_loop_list.size();
// //                fault_code_desc.add("{\"fault_code\": "+"fault code int"+",\"subcompo_m\": \""+"equipment name"+"\",\"active_i\": "+"true/false"+"}");
// //                numOfFault = fault_code_desc.size();
// //            } catch (Exception e){
// //                logger.error("pubCmdToWago - execJcEnqInfo - OPCUA Listening", e);
// //                wago.setWagoStatusCode(99999);
// //            }
// //
// //            /*
// //             * Extract Error Code to Error Msg
// //             */
// //            sJsonReturnMsg = "";
// //            sReplyTransID = "";
// //            if (hmDbParam.size()>0){hmDbParam.clear();}
// //            hmDbParam.put("faultCode", wago.getWagoStatusCode());
// //            if (wago.getWagoStatusCode() > 0) {
// //                try {
// //                    mi_i = "true";
// //                    wago.setErrorC("Failed");
// //                    status_code_list statusCodeDBResult = (status_code_list) dbCtrl.findEntity("status_code_list.findByStatusCode", hmDbParam);
// //                    if (statusCodeDBResult != null) {
// //                        //wago.setWagoStatusCodeMssg("Rejected: " + statusCodeDBResult.getStatus_code_mssg());
// //                    } else {
// //                        logger.error(String.format("pubCmdToWago - execJcEnqInfo - findEntity - Fault Code %s Not Found", wago.getWagoStatusCode()));
// //                        wago.setWagoStatusCodeMssg("Rejected: Fault Code Not Found");
// //                    }
// //                } catch (Exception e) {
// //                    logger.error("pubCmdToWago - execJcEnqInfo - findEntity", e);
// //                }
// //            } else if (numOfFault>0){
// //                wago.setErrorC("Failed");
// //                wago.setWagoStatusCodeMssg("Rejected: sub-component is having fault");
// //                mi_i = "true";
// //            }else if (numOfFault == 0){
// //                wago.setErrorC("OK");
// //                wago.setWagoStatusCodeMssg("");
// //                fault_code_desc = null;
// //            } else {
// //                logger.error(String.format("pubCmdToWago - execJcEnqInfo - findEntity - Fault Code %s Not Found", wago.getWagoStatusCode()));
// //                wago.setErrorC("Failed");
// //                wago.setWagoStatusCodeMssg("Rejected: Unknown error");
// //            }
// //
// //            jc_enq_info_r jcEnqInfoRDB = wago.getJcEnqInfo().generateJcEnqInfoR(wago.getErrorC(), wago.getWagoStatusCodeMssg(), jc_status_c, alert_mode,
// //                    mi_i, control_mode, numOfPm, pm_loop_list, numOfUturn, uturn_loop_list, numOfFault,
// //                    fault_code_desc);
// //
// //            try{
// //                dbCtrl.persistEntity(jcEnqInfoRDB);
// //            }catch (Exception e) {
// //                logger.error("pubCmdToWago - execJcEnqInfoR : JC_ENQ_INF0_R Data Insert DB Failed", e);
// //                wago.setWagoStatusCode(305);
// //            }
// //            sJsonReturnMsg = wago.getJcEnqInfo().generateReturnMsg(jcEnqInfoRDB);
// //            sReplyTransID = ConfigWago.getEsbJcEnqInfoR().toUpperCase();
// //            logger.debug("JC_ENQ_INFO_R pub message: \n" + sJsonReturnMsg);
// //            // Publish out to PSA sollace
// //            try{
// //                publishMessageToSolace(sReplyTransID, sJsonReturnMsg);
// //            }catch (Exception e) {
// //                logger.error("pubCmdToWago - execJcEnqInfo : ", e);
// //            }
// //        } catch (Exception e2) {
// //            logger.error("pubCmdToWago - execJcEnqInfo : " + e2.getMessage());
// //            Thread.currentThread().interrupt();
// //        } finally {
// //            // Close this thread, job completed.
// //            // EIUProcess2Thread.stop()
// //            Thread.currentThread().interrupt();
// //        }
//         try{
// //            EnqPushInfoThread enqpushinfothread = new EnqPushInfoThread(dbCtrl, esbClient,hmDbParam,opcUaClient);
// //            enqpushinfothread.start();
//             ExecutorService enqpushinfothread = Executors.newSingleThreadExecutor();
//             Future<Void> enqpushinfothreadFuture = enqpushinfothread.submit(new EnqPushInfoCallable(dbCtrl, esbClient,hmDbParam,opcUaClient));
            
        
//             enqpushinfothreadFuture.get();
//             enqpushinfothread.shutdownNow();
//         }catch (Exception e){
//             logger.error("pubCmdToWago - execJcEnqInfo : " + e.getMessage());
//             Thread.currentThread().interrupt();
//         } finally{
//             // Close this thread, job completed.
//             // EIUProcess2Thread.stop()
//             Thread.currentThread().interrupt();
//         }
//     }

//     private void execJcUpd() {
//         this.taskCompleted = false;
//         Boolean flagError = true;
//         payload = null;
//         sJsonReturnMsg = "";
//         sReplyTransID = "";
//         Gson gson = new Gson();
//         String curr_status = "";
//         String payloadXAckR = "{\"gtosplus_ops_header\":"+
//                 "{\"source_m\":\""+ConfigWago.getSourceM()+"\","+
//                 "\"event_dt\":\"\","+
//                 "\"event_id\":\"\","+
//                 "\"trans_id\":\"JC_ACK_R\""+
//                 "},"+
//                 "\"gtosplus_ops_body\":"+
//                 "{\"error_c\":\"\""+
//                 ",\"error_txt\":\"\""+
//                 ",\"jc_id\":"+ConfigWago.getJcId()+
//                 ",\"jc_m\":\""+ConfigWago.getJcM()+
//                 "\",\"prev_trans_id\":\"\"}}";
//         Payload payloadAckR = null;
//         try {
//             payloadAckR = gson.fromJson(payloadXAckR, Payload.class);
//         } catch (Exception e) {
//             logger.info(String.format("PubCmdToWago - ExecJcEnqInfo - payloadXLoop: %s", payloadXAckR), e);
//             return;
//         }
//         jcAckRM = payloadAckR.generatePayloadJcAckR();
//         jc_ack_r jcAckRDB = jcAckRM.generateJcAckR("Pend","","JC_UPD_ST");
//         try{
//                 dbCtrl.persistEntity(jcAckRDB);
//                 // logger.debug("jc_ack_r added");
//         }catch (Exception e) {
//             logger.error("pubCmdToWago - execJcUpd : JC_UPD_ST_R Data Insert DB Failed", e);
//             wago.setWagoStatusCode(305);
//         }
//         logger.debug("database ended");
//         sJsonReturnMsg = wago.getJcAckR().generateAckReturnMsg(jcAckRDB);
//         sReplyTransID = ConfigWago.getEsbJcAckR().toUpperCase();
//         logger.debug("JC_ACK_R - execJcUpd - pub message: \n" + sJsonReturnMsg);
//         // Publish out to PSA sollace
//         try{
//             publishMessageToSolace(sReplyTransID, sJsonReturnMsg);
//         }catch (Exception e) {
//             logger.error("pubCmdToWago - execJcUpd - jcAckR : ", e);
//         }
        
//         try{
//             // jcAckRM = payloadAckR.generatePayloadJcAckR();
//             // jc_ack_r jcAckRDB = jcAckRM.generateJcAckR("Pend","","JC_UPD_ST");
//             // try{
//             //     dbCtrl.persistEntity(jcAckRDB);
//             // }catch (Exception e) {
//             //     logger.error("pubCmdToWago - execJcUpd : JC_UPD_ST_R Data Insert DB Failed", e);
//             //     wago.setWagoStatusCode(305);
//             // }
//             // sJsonReturnMsg = wago.getJcAckR().generateAckReturnMsg(jcAckRDB);
//             // sReplyTransID = ConfigWago.getEsbJcAckR().toUpperCase();
//             // logger.debug("JC_ACK_R - execJcUpd - pub message: \n" + sJsonReturnMsg);
//             // // Publish out to PSA sollace
//             // try{
//             //     publishMessageToSolace(sReplyTransID, sJsonReturnMsg);
//             // }catch (Exception e) {
//             //     logger.error("pubCmdToWago - execJcUpd - jcAckR : ", e);
//             // }
//             if("PM_Red".equalsIgnoreCase(wago.getNewJcSt())) {
//                 try {
//                     writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL",true);
//                     curr_status = "PM_RED";
//                 } catch (Exception e) {
//                     logger.error("pubCmdToWago - execJcUpd : OPCUA write node failed:", e);
//                     flagError = true;
//                 }
//             } else{
//                 try {
//                     writeNode1.writeNodeValue1(opcUaClient, "|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL",false);
//                     curr_status = "PM_GREEN";
//                 } catch (Exception e) {
//                     logger.error("pubCmdToWago - execJcUpd : OPCUA write node failed:", e);
//                     flagError = true;
//                 }
//             }
//             //OPCUA listening here
//             while(flagError){
//                 handlerNode(opcUaClient,"acimitags.properties");
//                 for (String s : OPCUAWagoHM.OPCUAHm.keySet()) {
//                     if (s.equalsIgnoreCase("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Junction_Clr")) {
//                         if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
//                             logger.info("pubCmdToWago - execJcUpd - junction clear");
//                             wago.setNewJcSt(wago.getNewJcSt());
//                             flagError = false;
//                             break;
//                         }
//                     }
// //                    System.out.println(s + ":" + OPCUAWagoHM.OPCUAHm.get(s));
//                 }

//             }

//             /*
//              * Extract Error Code to Error Msg
//              */
//             sJsonReturnMsg = "";
//             sReplyTransID = "";
//             if (hmDbParam.size()>0){hmDbParam.clear();}
//             hmDbParam.put("faultCode", wago.getWagoStatusCode());
//             if (wago.getWagoStatusCode() > 0) {
//                 try {
//                     wago.setErrorC("Failed");
//                     status_code_list statusCodeDBResult = (status_code_list) dbCtrl.findEntity("status_code_list.findByStatusCode", hmDbParam);
//                     if (statusCodeDBResult != null) {
//                         //wago.setWagoStatusCodeMssg("Rejected: " + statusCodeDBResult.getStatus_code_mssg());
//                         wago.setWagoStatusDesc(statusCodeDBResult.getStatus_desc());
//                     } else {
//                         logger.info(String.format("pubCmdToWago - execJcUpd - findEntity - Fault Code %s Not Found", wago.getWagoStatusCode()));
//                         wago.setWagoStatusCodeMssg("Rejected: Fault Code Not Found");
//                     }
//                 } catch (Exception e) {
//                     logger.error("pubCmdToWago - execJcUpd - findEntity", e);
//                 }
//             } else{
//                 wago.setWagoStatusCodeMssg("");
//                 wago.setErrorC("OK");
//             }
//             jc_upd_st_r jcUpdStRDB = wago.getJcSt().generateJcUpdStR(wago.getErrorC(),wago.getWagoStatusCodeMssg(),wago.getNewJcSt());
//             try{
//                 dbCtrl.persistEntity(jcUpdStRDB);
//             }catch (Exception e) {
//                 logger.error("pubCmdToWago - execJcUpd : JC_UPD_ST_R Data Insert DB Failed", e);
//                 wago.setWagoStatusCode(305);
//             }
//             sJsonReturnMsg = wago.getJcSt().generateReturnMsg(jcUpdStRDB);
//             sReplyTransID = ConfigWago.getEsbJcUpdStR().toUpperCase();
//             logger.debug("JC_UPD_ST_R pub message: \n" + sJsonReturnMsg);
//             // Publish out to PSA sollace
//             try{
//                 publishMessageToSolace(sReplyTransID, sJsonReturnMsg);
//             }catch (Exception e) {
//                 logger.error("pubCmdToWago - execJcUpd : ", e);
//             }
//         } catch (Exception e2) {
//             logger.error("pubCmdToWago - execJcUpd : " + e2.getMessage());
//             Thread.currentThread().interrupt();
//         } finally {
//             // Close this thread, job completed.
//             // EIUProcess2Thread.stop()
//             Thread.currentThread().interrupt();
//         }
//     }

//     private void execJcRst(Wago wago) {
//         sJsonReturnMsg = "";
//         sReplyTransID = "";

//         try{
//             //OPCUA set all to default state

//             /*
//              * Extract Error Code to Error Msg
//              */
//             if (hmDbParam.size()>0){hmDbParam.clear();}
//             hmDbParam.put("faultCode", wago.getWagoStatusCode());
//             if (wago.getWagoStatusCode() > 0) {
//                 try {
//                     status_code_list statusCodeDBResult = (status_code_list) dbCtrl.findEntity("status_code_list.findByStatusCode", hmDbParam);
//                     if (statusCodeDBResult != null) {
//                         //wago.setWagoStatusCodeMssg(statusCodeDBResult.getStatus_code_mssg());
//                         wago.setWagoStatusDesc(statusCodeDBResult.getStatus_desc());
//                     } else {
//                         logger.info(String.format("pubCmdToWago - execJcUpd - findEntity - Fault Code %s Not Found", wago.getWagoStatusCode()));
//                         wago.setWagoStatusCodeMssg("Fault Code Not Found");
//                         wago.setWagoStatusDesc("09404");
//                     }
//                 } catch (Exception e) {
//                     logger.error("pubCmdToWago - execJcUpd - findEntity", e);
//                 }
//             } else {
//                 wago.setWagoStatusCodeMssg("OK");
//                 wago.setErrorC("");
//             }
//             /*
//              * Generate eqptInst Return Dataset
//              */

//             sJsonReturnMsg = "";
//             sReplyTransID = "";
//             jc_rst_alrm_r jcRstAlrmRDB = wago.getJcRstAlrm().generateJcRstAlrmR("OK",wago.getWagoStatusCodeMssg(),wago.getJcRstAlrm().getNum_of_faults(),wago.getJcRstAlrm().getFault_code_desc_list());
//             try{
//                 dbCtrl.persistEntity(jcRstAlrmRDB);
//             }catch (Exception e) {
//                 logger.info("pubCmdToWago - execJcUpd : JC_UPD_ST_R Data Insert DB Failed", e);
//                 wago.setWagoStatusCode(305);
//             }
//             sJsonReturnMsg = wago.getJcRstAlrm().generateFaultMsg(jcRstAlrmRDB);
//             sReplyTransID = ConfigWago.getEsbJcRstAlrmR().toUpperCase();
//             logger.debug("JC_RST_ALRM_R pub message: \n" + sJsonReturnMsg);
//             // Publish out to PSA sollace
//             try{
//                 publishMessageToSolace(sReplyTransID, sJsonReturnMsg);
//             }catch (Exception e) {
//                 logger.error("pubCmdToWago - execJcUpd : ", e);
//             }
//         } catch (Exception e2) {
//             logger.error("pubCmdToWago - execJcUpd : " + e2.getMessage());
//             Thread.currentThread().interrupt();
//         } finally {
//             // Close this thread, job completed.
//             // EIUProcess2Thread.stop()
//             Thread.currentThread().interrupt();
//         }
//     }

//     private void publishMessageToSolace(String transId, String message) {
//         try {
//             esbClient.tryPublishMessage(transId, message);
//         } catch (ESBClientException ex) {
//             logger.error("EIUProcessStep2 - publishMessageToSolace Error : " + ex.getMessage());
//         }
//     }



// }
