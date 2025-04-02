package module.Subscribenode;

import module.object.OPCUAWagoHM;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedSubscription;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

//import static org.eclipse.milo.examples.client.OPCUA.Subscribenode.singleSubscribe.subscribe;

public class multipleSubscribe {
    public static void handlerNode(OpcUaClient client,String configFile) {

        final CountDownLatch eventLatch = new CountDownLatch(1);

        //add subscription listener, handle reconnect and subscribe again problem
        //client.getSubscriptionManager().addSubscriptionListener(new Main.CustomSubscriptionListener(client));

        //handle manage event
        //handlerNode(client);

        //eventLatch.countDown();

        //keep listening
        //eventLatch.await();

        //System.out.println("Done");
        try {
            //create subscription
            ManagedSubscription subscription = ManagedSubscription.create(client);

            //needed key list
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(configFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> keys = new ArrayList<>();
            for (String key : props.stringPropertyNames()) {
                keys.add(props.getProperty(key));
            }

//            key.add("|var|WAGO 750-8202 PFC200 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_ZSO");
//            key.add("|var|WAGO 750-8202 PFC200 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_Cmd");

//            Properties props = new Properties();
//            LinkedHashMap<String, String> tagMap = new LinkedHashMap<>();
//            try (InputStream input = new FileInputStream("enqtags.properties")) {
////            Properties props = new Properties();
//                props.load(input);
//                for (String key : props.stringPropertyNames()) {
//                    String value = props.getProperty(key);
//                    tagMap.put(key, value);
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }

// Iterate over the tagMap and create the nodeIdList
//            List<NodeId> nodeIdList = new ArrayList<>();
//            for (String key : tagMap.keySet()) {
//                nodeIdList.add(new NodeId(4, key));
//            }

//            Collections.sort(nodeIdList, Comparator.comparing(nodeId -> nodeId.getIdentifier().toString()));

            List<NodeId> nodeIdList = new ArrayList<>();
            for (String s : keys) {
                nodeIdList.add(new NodeId(4, s));
                //System.out.println(nodeIdList);
            }
            for (NodeId nodeId : nodeIdList) {
                //System.out.println(nodeId);
            }
            Collections.sort(nodeIdList, Comparator.comparing(nodeId -> nodeId.getIdentifier().toString()));

            // Print the first node in the nodeIdList
//            NodeId firstNode = nodeIdList.get(0);
//            System.out.println(firstNode);
            if (OPCUAWagoHM.OPCUAHm.size()>0){
                OPCUAWagoHM.OPCUAHm.clear();
            }
//            if (nodeIdList.size()>0){
//                nodeIdList.clear();
//            }
            //listener
            List<ManagedDataItem> dataItemList = subscription.createDataItems(nodeIdList);
            for (ManagedDataItem managedDataItem : dataItemList) {
                managedDataItem.addDataValueListener((t) -> {
                    NodeId nodeId = managedDataItem.getNodeId();
                    String value = t.getValue().getValue().toString();

                    OPCUAWagoHM.OPCUAHm.put(String.valueOf(nodeId.getIdentifier().toString()),value);
//                    System.out.println(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode"));
//                    System.out.println(OPCUAWagoHM.OPCUAHm.get(String.valueOf(nodeId.getIdentifier().toString())));
//                    System.out.println(nodeId.getIdentifier().toString() + ":" + value);
//
//                    if (value.equals("false")) {
//                        System.out.println("Equipment OK");
//                    } else {
//                        System.out.println("Equipment having fault");
//                    }
                });
            }

//            for (String s : OPCUAWagoHM.OPCUAHm.keySet()) {
////                    if(OPCUAWagoHM.OPCUAHm.get(s) == "true"){
////                        wago.setWagoStatusCode(99999);
////                        fault_desc_list = new ArrayList<>();
////                        num_of_faults++;
////                        fault_desc_list.add("{\"fault_code\": "+99999+",\"subcompo_m\": \""+"TEST"+"\",\"active_i\": " +"false" + "}");
////                        logger.error("pubCmdToWago - execJcUpd - OPCUA : Sub-component ( "+s+" ) is having fault");
////                    }
////                    logger.debug(s+" - "+OPCUAWagoHM.OPCUAHm.get(s));
//                System.out.println(s + ":" + OPCUAWagoHM.OPCUAHm.get(s));
//            }

//            System.out.println(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.Operational_Mode"));
//            eventLatch.await(1, TimeUnit.SECONDS);

            Thread.sleep(1 * 1000L);
            subscription.deleteDataItems(dataItemList);
            subscription.delete();

//            ManagedDataItem Item1 = dataItemList.get(0);
//            Item1.addDataValueListener((t) -> {
//                NodeId nodeId = Item1.getNodeId();
//                String value = t.getValue().getValue().toString();
//                System.out.println(nodeId.getIdentifier().toString() + ":" + value);
//                if (value.equals("false")) {
//                    System.out.println("OK");
//                } else {
//                    System.out.println("Not OK");
//                }
//            });

//            ManagedDataItem Item2 = dataItemList.get(1);
//            Item2.addDataValueListener((t) -> {
//                NodeId nodeId = Item2.getNodeId();
//                String value = t.getValue().getValue().toString();
//                System.out.println(nodeId.getIdentifier().toString() + ":" + value);
//                if (value.equals("false")) {
//                    System.out.println("OK");
//                } else {
//                    System.out.println("Not OK");
//                }
//            });
////
//            ManagedDataItem Item3 = dataItemList.get(2);
//            Item3.addDataValueListener((t) -> {
//                NodeId nodeId = Item3.getNodeId();
//                String value = t.getValue().getValue().toString();
//                System.out.println(nodeId.getIdentifier().toString() + ":" + value);
//                if (value.equals("false")) {
//                    System.out.println("OK");
//                } else {
//                    System.out.println("Not OK");
//                }
//            });
//
//            ManagedDataItem Item4 = dataItemList.get(3);
//            Item4.addDataValueListener((t) -> {
//                NodeId nodeId = Item4.getNodeId();
//                String value = t.getValue().getValue().toString();
//                System.out.println(nodeId.getIdentifier().toString() + ":" + value);
//                if (value.equals("false")) {
//                    System.out.println("OK");
//                } else {
//                    System.out.println("Not OK");
//                }
//            });


//            ManagedDataItem firstItem = dataItemList.get(0);
//            firstItem.addDataValueListener((t) -> {
//                NodeId nodeId = firstItem.getNodeId();
//                String value = t.getValue().getValue().toString();
//                //System.out.println(nodeId.getIdentifier().toString() + ":" + value);
//
//                if (value.equals("true")) {
//                    System.out.println("apmsok");
//                } else {
//                    System.out.println("apmsmi");
//                }
//            });
//
//            ManagedDataItem secondItem = dataItemList.get(1);
//            secondItem.addDataValueListener((t) -> {
//                NodeId nodeId = secondItem.getNodeId();
//                String value = t.getValue().getValue().toString();
//                System.out.println(nodeId.getIdentifier().toString() + ":" + value);
//            });

            //subscribe(client);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
