package module.process;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.apache.log4j.Logger;

import module.config.ConfigWago;

public class HeapMemoryCheck implements Runnable{
    private static Logger logger = Logger.getLogger(HeapMemoryCheck.class.getName());

    private Thread memMonitoringThread = null;

    @Override
    public void run() {
        // TODO Auto-generated method stub
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        Double maxHeap = (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824;
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        while(true){
            try {
                //Monitor heap memory usage
                logger.info(String.format("Used heap memory: %.2f GB", (double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824));
                if ((double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824 >= maxHeap*0.8){
                    logger.warn(String.format("Used heap memory: %.2f GB", (double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824));
                    //Thread CPU usage
                    for(Long threadID : threadMXBean.getAllThreadIds()) {
                        ThreadInfo info = threadMXBean.getThreadInfo(threadID);
                        logger.info("Thread name: " + info.getThreadName() + ", Thread State: " + info.getThreadState() + String.format("CPU time: %s ns", threadMXBean.getThreadCpuTime(threadID)));
                    }
                }
            
                Thread.sleep(ConfigWago.getTriggerCheck() * 1000L);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public void start() {
        if (memMonitoringThread == null) {
            memMonitoringThread = new Thread(this, "MemoryMonitoringThread");
            memMonitoringThread.start();
        }
    }

    public void stop() {
        if (memMonitoringThread != null) {
            memMonitoringThread = new Thread(this, "MemoryMonitoringThread");
            memMonitoringThread.interrupt();
        }
    }
}
