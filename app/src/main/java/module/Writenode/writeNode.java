package module.Writenode;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


//import static org.eclipse.milo.examples.client.OPCUA.Main.managedSubscriptionEvent;

public class writeNode {
    public static void writeNodeValue(OpcUaClient client) throws Exception {
        //nodes
//        NodeId nodeId = new NodeId(4, "|var|WAGO 750-8202 PFC200 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL");
//        String identifier = "|var|WAGO 750-8202 PFC200 2ETH RS XTR.Application.OPC_UA.PM_RED_SIGNAL";
//        Boolean i = false;
//        Variant v = new Variant(i);
//        //must define data type, else will error, cannot write
//        DataValue nowValue = new DataValue(v, null, null);
//        //write nodes
//        StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
//        System.out.println("-----WriteValue-----");
//        System.out.println(identifier + ": " + String.valueOf(nowValue.getValue().getValue()));
//        System.out.println("\n");
        //Thread.sleep(20);
        //managedSubscriptionEvent(client);

//        Map<String, Boolean> nodeValues = new HashMap<>();
//        try (InputStream inputStream = new FileInputStream("resettags.properties")) {
//            Properties properties = new Properties();
//            properties.load(inputStream);
//            for (String key : properties.stringPropertyNames()) {
//                nodeValues.put(key, Boolean.parseBoolean(properties.getProperty(key)));
//            }
//        }
//
//        for (Map.Entry<String, Boolean> entry : nodeValues.entrySet()) {
//            String nodeIdentifier = entry.getKey();
//            Boolean nodeValue = entry.getValue();
//            NodeId nodeId = new NodeId(2, nodeIdentifier);
//            Variant variant = new Variant(nodeValue);
//            DataValue dataValue = new DataValue(variant, null, null);
//            StatusCode statusCode = client.writeValue(nodeId, dataValue).join();
//            System.out.println("-----WriteValue-----");
//            System.out.println(nodeIdentifier + ": " + String.valueOf(nodeValue));
//            System.out.println("\n");
//        }

        Properties props = new Properties();
        LinkedHashMap<String, String> tagMap = new LinkedHashMap<>();
        try (InputStream input = new FileInputStream("resettags.properties")) {
//            Properties props = new Properties();
            props.load(input);
            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                tagMap.put(key, value);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

// Iterate over the tagMap and create the nodeIdList
        List<NodeId> nodeIdList = new ArrayList<>();
        for (String key : tagMap.keySet()) {
            nodeIdList.add(new NodeId(4, key));
        }

        //Collections.sort(nodeIdList, Comparator.comparing(nodeId -> nodeId.getIdentifier().toString()));
        //System.out.println(nodeIdList);d

        for (Map.Entry<String, String> entry : tagMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            NodeId nodeId = new NodeId(2, key);
            Variant variant = new Variant(value);
            DataValue dataValue = new DataValue(variant);

            StatusCode statusCode = client.writeValue(nodeId, dataValue).get();
            if (statusCode.isGood()) {
                System.out.println("Value written successfully to node " + nodeId.getIdentifier().toString() + ":" + value);
            } else {
                System.out.println("Failed to write value to node " + nodeId.getIdentifier().toString() + ": " + statusCode.toString() + ":" + value);
            }
        }

//        List<String> values = Arrays.asList("value1", "value2", "value3");
//
//        // Write values to nodes
//        for (int i = 0; i < nodeIdList.size() && i < values.size(); i++) {
//            NodeId nodeId = nodeIdList.get(i);
//            String value = values.get(i);
//            Variant v = new Variant(value);
//            DataValue nowValue = new DataValue(v, null, null);
//            //write nodes
//            StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
//            System.out.println("-----WriteValue-----");
//            System.out.println(nodeId + ": " + String.valueOf(nowValue.getValue().getValue()));
//            System.out.println("\n");
//        }

//        Properties props = new Properties();
//        try {
//            props.load(new FileInputStream("resettags.properties"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        List<String> keys = new ArrayList<>();
//        for (String key : props.stringPropertyNames()) {
//            keys.add(props.getProperty(key));
//        }
//
//        List<NodeId> nodeIdList = new ArrayList<>();
//        for (String s : keys) {
//            nodeIdList.add(new NodeId(2, s));
//            System.out.println(nodeIdList);
//        }
//
//        NodeId Item1 = nodeIdList.get(0);
//        String i = "12345678";
//        Variant v = new Variant(i);
//        //must define data type, else will error, cannot write
//        DataValue nowValue = new DataValue(v, null, null);
//        //write nodes
//        StatusCode statusCode = client.writeValue(Item1, nowValue).join();
//        System.out.println("-----WriteValue-----");
//        System.out.println(Item1 + ": " + String.valueOf(nowValue.getValue().getValue()));
//        System.out.println("\n");
//
//        NodeId Item2 = nodeIdList.get(1);
//        String i2 = "Line Sensor";
//        Variant v2 = new Variant(i2);
//        //must define data type, else will error, cannot write
//        DataValue nowValue2 = new DataValue(v2, null, null);
//        //write nodes
//        StatusCode statusCode2 = client.writeValue(Item2, nowValue2).join();
//        System.out.println("-----WriteValue-----");
//        System.out.println(Item2 + ": " + String.valueOf(nowValue2.getValue().getValue()));
//        System.out.println("\n");
//
//        NodeId Item3 = nodeIdList.get(2);
//        Boolean i3 = false;
//        Variant v3 = new Variant(i3);
//        //must define data type, else will error, cannot write
//        DataValue nowValue3 = new DataValue(v3, null, null);
//        //write nodes
//        StatusCode statusCode3 = client.writeValue(Item3, nowValue3).join();
//        System.out.println("-----WriteValue-----");
//        System.out.println(Item3 + ": " + String.valueOf(nowValue3.getValue().getValue()));
//        System.out.println("\n");
//
//        NodeId Item4 = nodeIdList.get(3);
//        Boolean i4 = false;
//        Variant v4 = new Variant(i4);
//        //must define data type, else will error, cannot write
//        DataValue nowValue4 = new DataValue(v4, null, null);
//        //write nodes
//        StatusCode statusCode4 = client.writeValue(Item4, nowValue4).join();
//        System.out.println("-----WriteValue-----");
//        System.out.println(Item4 + ": " + String.valueOf(nowValue4.getValue().getValue()));
//        System.out.println("\n");
//
//        NodeId Item5 = nodeIdList.get(4);
//        String i5 = "Alarm failed";
//        Variant v5 = new Variant(i5);
//        //must define data type, else will error, cannot write
//        DataValue nowValue5 = new DataValue(v5, null, null);
//        //write nodes
//        StatusCode statusCode5 = client.writeValue(Item5, nowValue5).join();
//        System.out.println("-----WriteValue-----");
//        System.out.println(Item5 + ": " + String.valueOf(nowValue5.getValue().getValue()));
//        System.out.println("\n");


//        List<ManagedDataItem> dataItemList = subscription.createDataItems(nodeIdList);
//
//        List<WriteValue> writeValueList = new ArrayList<>();
//        for (String key : props.stringPropertyNames()) {
//            NodeId nodeId = new NodeId(2, key);
//            Variant value = new Variant(props.getProperty(key));
//            WriteValue writeValue = new WriteValue(nodeId, null, null, value);
//            writeValueList.add(writeValue);
//        }
//
//        StatusCode[] results = client.write(writeValueList.toArray(new WriteValue[0]));
//        for (StatusCode result : results) {
//            if (result.isGood()) {
//                System.out.println("Write succeeded");
//            } else {
//                System.out.println("Write failed: " + result);
//            }
    }
}
