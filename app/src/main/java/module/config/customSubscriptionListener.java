package module.config;

import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class customSubscriptionListener implements UaSubscriptionManager.SubscriptionListener{
    private static final Logger logger = Logger.getLogger(ConfigSetup.class.getName());
    private OpcUaClient client;

    public customSubscriptionListener(OpcUaClient client) {
        this.client = client;
    }


    public void onKeepAlive(UaSubscription subscription, DateTime publishTime) {
        logger.info("onKeepAlive");
    }

    public void onStatusChanged(UaSubscription subscription, StatusCode status) {
        logger.info("onStatusChanged");
    }

    public void onPublishFailure(UaException exception) {
        logger.error("onPublishFailure");
    }

    public void onNotificationDataLost(UaSubscription subscription) {
        logger.error("onNotificationDataLost");
    }

    /**
    * when reconnect and try to recover fails, use this method
    * @param uaSubscription subscribe
    * @param statusCode status
    */
    public void onSubscriptionTransferFailed(UaSubscription uaSubscription, StatusCode statusCode) {
        logger.warn("Reconnect fails, need subscribe again");
        //subscribe again
        subscribeNode.handlerNode(client);
    }

}
