package module.process;

//import module.object.OPCUAWagodraft;
import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.ServerState;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import module.config.ConfigSetup;

public class readNode {
    private static final Logger logger = Logger.getLogger(ConfigSetup.class.getName());
    
    public static void readNode(OpcUaClient client) throws Exception {
        int namespaceIndex = 2;
//        OPCUAWagodraft.setJunctionStatus("");
        String identifier = "[APMS]/SMS/Session_Lock";
        //nodes
        NodeId nodeId = new NodeId(namespaceIndex, identifier);
        //read nodes
        DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, nodeId).get();
        //identifier
        identifier = String.valueOf(nodeId.getIdentifier());
        NodeId nodeId1 = new NodeId(2,"[APMS]/SMS");
//        key.add("|var|WAGO 750-8202 PFC200 2ETH RS XTR.Application.OPC_UA.APJ24_LN_001A_BARR_001_Simulate");
        List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(nodeId1);
        logger.info("-----ReadValue-----");
        for(UaNode node:nodes){
            NodeId nodeId2 = node.getNodeId();
            if(node.getNodeClass() == NodeClass.Variable){
                DataValue value2 = client.readValue(0.0, TimestampsToReturn.Both, nodeId2).get();
                logger.info(nodeId2.getIdentifier().toString() + ": "+ value2.getValue().getValue());
            }
        }

        // asynchronous read request
        readServerStateAndTime(client).thenAccept(values -> {
            DataValue v0 = values.get(0);
            DataValue v1 = values.get(1);

            logger.info("State= " + ServerState.from((Integer) v0.getValue().getValue()));
            logger.info("CurrentTime= "+ v1.getValue().getValue());
            logger.info("\n");

        });
    }

    private static CompletableFuture<List<DataValue>> readServerStateAndTime(OpcUaClient client) {
        List<NodeId> nodeIds = ImmutableList.of(
                Identifiers.Server_ServerStatus_State,
                Identifiers.Server_ServerStatus_CurrentTime);

        return client.readValues(0.0, TimestampsToReturn.Both, nodeIds);
    }
}
