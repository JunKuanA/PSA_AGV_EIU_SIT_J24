package module.process;

import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import module.config.ConfigSetup;

public class writeNode {
    private static final Logger logger = Logger.getLogger(ConfigSetup.class.getName());

    public static void writeNodeValue(OpcUaClient client) throws Exception {
        //nodes
        NodeId nodeId = new NodeId(2, "[APMS]/SMS/Session_Lock");
//        key.add("|var|WAGO 750-8202 PFC200 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_Simulate");
        String identifier = "[APMS]/SMS/Session_Lock";
        Short i = 3;
        Variant v = new Variant("true");
        //must define data type, else will error, cannot write
        DataValue nowValue = new DataValue(v, null, null);
        //write nodes
        StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
        logger.info("-----WriteValue-----");
        logger.info(identifier + ": " + String.valueOf(nowValue.getValue().getValue()));
        logger.info("\n");
        Thread.sleep(20);
    }

    public static void writeNodeValue1(OpcUaClient client) throws Exception {
        //nodes
        NodeId nodeId = new NodeId(2, "[APMS]/SMS/Session_Lock");
//        key.add("|var|WAGO 750-8202 PFC200 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_Simulate");
        String identifier = "[APMS]/SMS/Session_Lock";
        Short i = 3;
        Variant v = new Variant("false");
        //must define data type, else will error, cannot write
        DataValue nowValue = new DataValue(v, null, null);
        //write nodes
        StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
        logger.info("-----WriteValue-----");
        logger.info(identifier + ": " + String.valueOf(nowValue.getValue().getValue()));
        logger.info("\n");
    }
}
