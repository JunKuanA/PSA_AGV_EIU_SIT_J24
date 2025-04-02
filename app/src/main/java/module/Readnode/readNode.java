package module.Readnode;

import com.google.common.collect.ImmutableList;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.ServerState;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class readNode {
    public static void readNode(OpcUaClient client) throws Exception {
//        int namespaceIndex = 2;
//        String identifier = "[APMS]/SMS/Send_Trigger";
//        //node
//        NodeId nodeId = new NodeId(namespaceIndex, identifier);
//        //read node value
//        DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, nodeId).get();
//        //identifier
//        identifier = String.valueOf(nodeId.getIdentifier());
//        System.out.println(identifier + ": " + String.valueOf(value.getValue().getValue()));

//        Properties props = new Properties();
//        try {
//            props.load(new FileInputStream("enqtags.properties"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        List<String> keys = new ArrayList<>();
//        for (String key : props.stringPropertyNames()) {
//            keys.add(props.getProperty(key));
//        }
//
//        List<NodeId> nodeIdList = new ArrayList<>();
//        for (String s : keys) {
//            nodeIdList.add(new NodeId(2, s));
//            //System.out.println(nodeIdList);
//        }
//        for (NodeId nodeId : nodeIdList) {
//            System.out.println(nodeId);
//        }
//        Collections.sort(nodeIdList, Comparator.comparing(nodeId -> nodeId.getIdentifier().toString()));
//
//        List<ManagedDataItem> dataItemList = subscription.createDataItems(nodeIdList);
//
//        for (ManagedDataItem managedDataItem : dataItemList) {
//                managedDataItem.addDataValueListener((t) -> {
//                    NodeId nodeId = managedDataItem.getNodeId();
//                    String value = t.getValue().getValue().toString();
//                    System.out.println(nodeId.getIdentifier().toString() + ":" + value);
//
//                    if (value.equals("true")) {
//                        System.out.println("apmsok");
//                    } else {
//                        System.out.println("apmsmi");
//                    }
//                });
//            }
    }
}
