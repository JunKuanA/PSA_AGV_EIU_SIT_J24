package module.Subscribenode;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class singleSubscribe {
    public static void subscribe(OpcUaClient client) throws Exception {
        AtomicInteger a=new AtomicInteger();
        //create subscription
        client

                .getSubscriptionManager()
                .createSubscription(1000.0)
                .thenAccept(t -> {
                    //node
                    //HashMap<String, String> map = new HashMap<>();

                    // Adding elements to the Map
                    // using standard put() method
                    //map.put("[APMS]/SMS/Send_Trigger","1234567890");
                    //map.put("[PLC]/text","lidar sensor");
                    //map.put("[PLC]/description","fault");

                    // Print size and content of the Map
                    //System.out.println("Size of map is:- "+ map.size());

                    // Printing elements in object of Map
                    //System.out.println(map);

                    // Print keys and values
                    NodeId nodeId = new NodeId(4, "|var|WAGO 750-8202 PFC200 2ETH RS XTR.Application.OPC_UA.MG_Gate_CLOSE");
//                    key.add("|var|WAGO 750-8202 PFC200 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_Simulate");
                    ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);
                    //create monitoring parameters
                    MonitoringParameters parameters = new MonitoringParameters(UInteger.valueOf(a.getAndIncrement()), 1000.0, null, UInteger.valueOf(10), true);
                    //create MonitoredItemCreateRequest
                    //this request use to create subscription at the end
                    MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
                    List<MonitoredItemCreateRequest> requests = new ArrayList<>();
                    requests.add(request);
                    //create monitored items, subscribe to the node
                    t.createMonitoredItems(
                            TimestampsToReturn.Both,
                            requests,
                            (item, id) -> item.setValueConsumer((it, val) -> {
                                //System.out.println("-----Subscribe-----");
                                //System.out.println("nodeid :" + it.getReadValueId().getNodeId() + " value :" + val.getValue());
                                System.out.println("\n");
                                String b = val.getValue().getValue().toString();
                                if (b == "true") {
                                    try {
//                                        writeNodeValue(client);
                                        //readNode(client);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                } else if (b == "f") {
                                    try {
//                                        writeNodeValue1(client);
                                        //readNode(client);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            })
                    );
                }).get();

        //keep subscripting
        Thread.sleep(Long.MAX_VALUE);
    }
}
