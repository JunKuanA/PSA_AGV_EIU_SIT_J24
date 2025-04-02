package module.Writenode;

import module.process.ProcessMsg;
import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;


public class writeNode1 {
    private String identifier = "";
    private static Logger logger = Logger.getLogger(writeNode1.class.getName());
    public static void writeNodeValue1(OpcUaClient client, String identifier,Boolean flag) throws Exception {
        //nodes
        NodeId nodeId = new NodeId(4, identifier);
        Boolean i =flag;
        Variant v = new Variant(i);
        //must define data type, else will error, cannot write
        DataValue nowValue = new DataValue(v, null, null);
        //write nodes
        StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
        logger.info("-----WriteValue-----");
        logger.info(identifier + ": " + String.valueOf(nowValue.getValue().getValue()));
    }
}
