package module.config;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

public class managedSubscriptionEvent {
    private static final Logger logger = Logger.getLogger(ConfigSetup.class.getName());
    /**
     * manage subscription event multiple subscription
     *
     * @param client
     * @throws Exception
     */
    public static void managedSubscriptionEvent(OpcUaClient client) throws Exception {
        final CountDownLatch eventLatch = new CountDownLatch(1);

        //add subscription listener, handle reconnect and subscribe again problem
        client.getSubscriptionManager().addSubscriptionListener(new customSubscriptionListener(client));

        //handle manage event
        subscribeNode.handlerNode(client);

        //keep listening
        eventLatch.await();

    }
}
