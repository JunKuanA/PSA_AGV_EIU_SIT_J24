package module.process;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import module.entities.equipment_info;
import module.entities.persistenceManager;
import module.object.OPCUAWagoHM;
import static module.Subscribenode.multipleSubscribe.handlerNode;

public class HBPublishThread implements Runnable{

    private static Logger logger = Logger.getLogger(HBPublishThread.class.getName());
    private persistenceManager persistenceManager = null;
    private Thread eiuHBPublishThread = null;
    private OpcUaClient opcUaClient = null;


    public HBPublishThread(persistenceManager persistenceManager, OpcUaClient opcUaClient) {
        this.persistenceManager = persistenceManager;
        this.opcUaClient = opcUaClient;
    }

    public void start() {
        if (eiuHBPublishThread == null) {
            eiuHBPublishThread = new Thread(this, "eiuHBPublishThread");
            eiuHBPublishThread.start();
        }
    }

    public void stop() {
        if (eiuHBPublishThread == null) {
            eiuHBPublishThread = new Thread(this, "eiuHBPublishThread");
            eiuHBPublishThread.interrupt();
        }
    }

    @Override
    public void run() {
        List<equipment_info> equipmentInfo = persistenceManager.getAllHB();
        int heartbeatRunningNumber = equipmentInfo.get(2).getLast_hb();
        while(true){
            // handlerNode(opcUaClient,"acihbtags.properties");
            try {
                heartbeatRunningNumber = (heartbeatRunningNumber + 1) % 10000;
                try {
                    persistenceManager.updateEIU1HB(heartbeatRunningNumber);
                } catch (Exception e) {
                    logger.error("HBPublishThread - run : EIU heartbeat lost", e);
                }
                
                // try {
                //     persistenceManager.updatePLCHB(Integer.parseInt(OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PLC_Hearbeat")));
                //     logger.debug("PLC HB: "+OPCUAWagoHM.OPCUAHm.get("|var|WAGO 750-8212 PFC200 G2 2ETH RS XTR.Application.OPC_UA.PLC_Hearbeat"));
                // }catch(NumberFormatException ne){
    
                // }
                // catch (Exception e) {
                //     logger.error("HBPublishThread - run : PLC heartbeat lost", e);
                // }
                Thread.sleep(1 * 1000L);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }      
    
}
