package module.config;

import java.util.List;


public class ConfigWago {
    private ConfigWago(){
    }

    private static final String WAGOTIMEFORMAT = "yyyyMMddHHmmssS";
    private static final String WAGOINFOTIMEFORMAT = "yyyyMMddHHmm";
    private static final String EVENTIDTIMEFORMAT = "yyyyMMdd'T'HHmmss-SSS";
    private static final String EVENTDTTIMEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private static final int FAULTCODE_INVALIDGATEID = 300;
    private static final int FAULTCODE_INVALIDMESSAGEFORMAT = 301;
    private static final int FAULTCODE_INVALIDTRANSID = 302;
    private static final int FAULTCODE_INVALIDEQUIPMENTNAME = 303;
    private static final int FAULTCODE_REQUESTTYPE = 304;
    private static final int FAULTCODE_UNKNOWNERROR = 305;
    private static final int FAULTCODE_INVALIDREQUEST = 306;
    private static final int FAULTCODE_DUPLICATEENTRY = 313;
    private static final int FAULTCODE_TIMEOUTFROMPLC = 901;
    private static final int FAULTCODE_REQUESTINTERRUPTED = 902;
    private static final int FAULTCODE_PLCCOMMUNICATIONLOST = 903;
    private static final int FAULTCODE_PLCCOMMUNICATIONRECOVERED = 904;
    private static final int FAULTCODE_NOTFOUND = 999;

    private static String esbJcUpdSt;
    private static String esbTjChgModeR;
    private static String esbJcUpdStR;
    private static String esbJcEnqInfo;
    private static String esbJcEnqInfoR;
    private static String esbJcRstAlrm;
    private static String esbJcRstAlrmR;
    private static String esbJcAckR;
    private static String esbJcHrtbtInfo;
    private static String esbJcAlrmInfo;
    private static String esbJcPmDtecInfo;
    private static String esbJcUturnDtecInfo;

    private static String OPCUAUrl;
    private static String mqttBrokerUser;
    private static String mqttBrokerPassword;
    private static int keepAliveInterval;
    private static int cleanSession;
    private static int maxInFlight;
    private static int qos;

    private static String gateType;
    private static String mqttPublisherClientId;
    private static String mqttSubscriberClientId;
    private static String mqttProcessClientId;
    private static String publishHeartbeatTopic;
    private static String subscribeHeartbeatTopic;
    private static String subscribeInfoTopic;
    private static String publishGateTopic;
    private static String subscribeGateTopic;
    private static String subscribeLightTopic;
    private static int triggerCheck;
    private static int maxHeartbeatTolerance;
    private static int infoTolerance;
    private static int threadTimeout;
    private static List<String> deviceList;

    private static int messageCheckerEnabled;
    private static int messageCheckerInterval;
    private static int messageRetries;
    private static int ackTolerance;
    private static int ignoreTolerance;

    private static String sourceM;
    private static String jcM;    
    private static int jcId;

    private static String tjM;
    private static int tjId;

    private static String eqptClass;

    private static String caCrtFile;
    private static String clientCrtFile;
    private static String clientKeyFile;
    private static String sslPassword;

    private static String persistenceUnitName;

    
    public static int getJcId() {
        return jcId;
    }

    public static void setJcId(int jcId) {
        ConfigWago.jcId = jcId;
    }

    public static int getTjId() {
        return tjId;
    }

    public static void setTjId(int tjId) {
        ConfigWago.tjId = tjId;
    }


    public static String getWagotimeformat() {
        return WAGOTIMEFORMAT;
    }

    public static String getTjM() {
        return tjM;
    }

    public static void setTjM(String tjM) {
        ConfigWago.tjM = tjM;
    }

    public static String getWagoinfotimeformat() {
        return WAGOINFOTIMEFORMAT;
    }

    public static String getEventidtimeformat() {
        return EVENTIDTIMEFORMAT;
    }

    public static String getEventdttimeformat() {
        return EVENTDTTIMEFORMAT;
    }

    public static int getFaultcodeInvalidgateid() {
        return FAULTCODE_INVALIDGATEID;
    }

    public static int getFaultcodeInvalidmessageformat() {
        return FAULTCODE_INVALIDMESSAGEFORMAT;
    }

    public static int getFaultcodeInvalidtransid() {
        return FAULTCODE_INVALIDTRANSID;
    }

    public static int getFaultcodeInvalidequipmentname() {
        return FAULTCODE_INVALIDEQUIPMENTNAME;
    }

    public static int getFaultcodeRequesttype() {
        return FAULTCODE_REQUESTTYPE;
    }

    public static int getFaultcodeUnknownerror() {
        return FAULTCODE_UNKNOWNERROR;
    }

    public static int getFaultcodeInvalidrequest() {
        return FAULTCODE_INVALIDREQUEST;
    }

    public static int getFaultcodeDuplicateentry() {
        return FAULTCODE_DUPLICATEENTRY;
    }

    public static int getFaultcodeTimeoutfromplc() {
        return FAULTCODE_TIMEOUTFROMPLC;
    }

    public static int getFaultcodeRequestinterrupted() {
        return FAULTCODE_REQUESTINTERRUPTED;
    }

    public static int getFaultcodePlccommunicationlost() {
        return FAULTCODE_PLCCOMMUNICATIONLOST;
    }

    public static int getFaultcodePlccommunicationrecovered() {
        return FAULTCODE_PLCCOMMUNICATIONRECOVERED;
    }

    public static int getFaultcodeNotfound() {
        return FAULTCODE_NOTFOUND;
    }

    public static String getEsbJcUpdSt() {
        return esbJcUpdSt;
    }

    public static void setEsbJcUpdSt(String esbJcUpdSt) {
        ConfigWago.esbJcUpdSt = esbJcUpdSt;
    }

    public static String getEsbTjChgModeR() {
        return esbTjChgModeR;
    }

    public static void setEsbTjChgModeR(String esbTjChgModeR) {
        ConfigWago.esbTjChgModeR = esbTjChgModeR;
    }

    public static String getEsbJcUpdStR() {
        return esbJcUpdStR;
    }

    public static void setEsbJcUpdStR(String esbJcUpdStR) {
        ConfigWago.esbJcUpdStR = esbJcUpdStR;
    }

    public static String getEsbJcEnqInfo() {
        return esbJcEnqInfo;
    }

    public static void setEsbJcEnqInfo(String esbJcEnqInfo) {
        ConfigWago.esbJcEnqInfo = esbJcEnqInfo;
    }

    public static String getEsbJcEnqInfoR() {
        return esbJcEnqInfoR;
    }

    public static void setEsbJcEnqInfoR(String esbJcEnqInfoR) {
        ConfigWago.esbJcEnqInfoR = esbJcEnqInfoR;
    }

    public static String getEsbJcRstAlrm() {
        return esbJcRstAlrm;
    }

    public static void setEsbJcRstAlrm(String esbJcRstAlrm) {
        ConfigWago.esbJcRstAlrm = esbJcRstAlrm;
    }

    public static String getEsbJcRstAlrmR() {
        return esbJcRstAlrmR;
    }

    public static void setEsbJcRstAlrmR(String esbJcRstAlrmR) {
        ConfigWago.esbJcRstAlrmR = esbJcRstAlrmR;
    }

    public static String getEsbJcAckR() {
        return esbJcAckR;
    }

    public static void setEsbJcAckR(String esbJcAckR) {
        ConfigWago.esbJcAckR = esbJcAckR;
    }

    public static String getEsbJcHrtbtInfo() {
        return esbJcHrtbtInfo;
    }

    public static void setEsbJcHrtbtInfo(String esbJcHrtbtInfo) {
        ConfigWago.esbJcHrtbtInfo = esbJcHrtbtInfo;
    }

    public static String getOPCUAUrl() {
        return OPCUAUrl;
    }

    public static void setOPCUAUrl(String oPCUAUrl) {
        OPCUAUrl = oPCUAUrl;
    }

    public static String getMqttBrokerUser() {
        return mqttBrokerUser;
    }

    public static void setMqttBrokerUser(String mqttBrokerUser) {
        ConfigWago.mqttBrokerUser = mqttBrokerUser;
    }

    public static String getMqttBrokerPassword() {
        return mqttBrokerPassword;
    }

    public static void setMqttBrokerPassword(String mqttBrokerPassword) {
        ConfigWago.mqttBrokerPassword = mqttBrokerPassword;
    }

    public static int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public static void setKeepAliveInterval(int keepAliveInterval) {
        ConfigWago.keepAliveInterval = keepAliveInterval;
    }

    public static int getCleanSession() {
        return cleanSession;
    }

    public static void setCleanSession(int cleanSession) {
        ConfigWago.cleanSession = cleanSession;
    }

    public static int getMaxInFlight() {
        return maxInFlight;
    }

    public static void setMaxInFlight(int maxInFlight) {
        ConfigWago.maxInFlight = maxInFlight;
    }

    public static int getQos() {
        return qos;
    }

    public static void setQos(int qos) {
        ConfigWago.qos = qos;
    }

    public static String getGateType() {
        return gateType;
    }

    public static void setGateType(String gateType) {
        ConfigWago.gateType = gateType;
    }

    public static String getMqttPublisherClientId() {
        return mqttPublisherClientId;
    }

    public static void setMqttPublisherClientId(String mqttPublisherClientId) {
        ConfigWago.mqttPublisherClientId = mqttPublisherClientId;
    }

    public static String getMqttSubscriberClientId() {
        return mqttSubscriberClientId;
    }

    public static void setMqttSubscriberClientId(String mqttSubscriberClientId) {
        ConfigWago.mqttSubscriberClientId = mqttSubscriberClientId;
    }

    public static String getMqttProcessClientId() {
        return mqttProcessClientId;
    }

    public static void setMqttProcessClientId(String mqttProcessClientId) {
        ConfigWago.mqttProcessClientId = mqttProcessClientId;
    }

    public static String getPublishHeartbeatTopic() {
        return publishHeartbeatTopic;
    }

    public static void setPublishHeartbeatTopic(String publishHeartbeatTopic) {
        ConfigWago.publishHeartbeatTopic = publishHeartbeatTopic;
    }

    public static String getSubscribeHeartbeatTopic() {
        return subscribeHeartbeatTopic;
    }

    public static void setSubscribeHeartbeatTopic(String subscribeHeartbeatTopic) {
        ConfigWago.subscribeHeartbeatTopic = subscribeHeartbeatTopic;
    }

    public static String getSubscribeInfoTopic() {
        return subscribeInfoTopic;
    }

    public static void setSubscribeInfoTopic(String subscribeInfoTopic) {
        ConfigWago.subscribeInfoTopic = subscribeInfoTopic;
    }

    public static String getPublishGateTopic() {
        return publishGateTopic;
    }

    public static void setPublishGateTopic(String publishGateTopic) {
        ConfigWago.publishGateTopic = publishGateTopic;
    }

    public static String getSubscribeGateTopic() {
        return subscribeGateTopic;
    }

    public static void setSubscribeGateTopic(String subscribeGateTopic) {
        ConfigWago.subscribeGateTopic = subscribeGateTopic;
    }

    public static String getSubscribeLightTopic() {
        return subscribeLightTopic;
    }

    public static void setSubscribeLightTopic(String subscribeLightTopic) {
        ConfigWago.subscribeLightTopic = subscribeLightTopic;
    }

    public static int getTriggerCheck() {
        return triggerCheck;
    }

    public static void setTriggerCheck(int triggerCheck) {
        ConfigWago.triggerCheck = triggerCheck;
    }

    public static int getMaxHeartbeatTolerance() {
        return maxHeartbeatTolerance;
    }

    public static void setMaxHeartbeatTolerance(int maxHeartbeatTolerance) {
        ConfigWago.maxHeartbeatTolerance = maxHeartbeatTolerance;
    }

    public static int getInfoTolerance() {
        return infoTolerance;
    }

    public static void setInfoTolerance(int infoTolerance) {
        ConfigWago.infoTolerance = infoTolerance;
    }

    public static int getThreadTimeout() {
        return threadTimeout;
    }

    public static void setThreadTimeout(int threadTimeout) {
        ConfigWago.threadTimeout = threadTimeout;
    }

    public static List<String> getDeviceList() {
        return deviceList;
    }

    public static void setDeviceList(List<String> deviceList) {
        ConfigWago.deviceList = deviceList;
    }

    public static int getMessageCheckerEnabled() {
        return messageCheckerEnabled;
    }

    public static void setMessageCheckerEnabled(int messageCheckerEnabled) {
        ConfigWago.messageCheckerEnabled = messageCheckerEnabled;
    }

    public static int getMessageCheckerInterval() {
        return messageCheckerInterval;
    }

    public static void setMessageCheckerInterval(int messageCheckerInterval) {
        ConfigWago.messageCheckerInterval = messageCheckerInterval;
    }

    public static int getMessageRetries() {
        return messageRetries;
    }

    public static void setMessageRetries(int messageRetries) {
        ConfigWago.messageRetries = messageRetries;
    }

    public static int getAckTolerance() {
        return ackTolerance;
    }

    public static void setAckTolerance(int ackTolerance) {
        ConfigWago.ackTolerance = ackTolerance;
    }

    public static int getIgnoreTolerance() {
        return ignoreTolerance;
    }

    public static void setIgnoreTolerance(int ignoreTolerance) {
        ConfigWago.ignoreTolerance = ignoreTolerance;
    }

    public static String getSourceM() {
        return sourceM;
    }

    public static void setSourceM(String sourceM) {
        ConfigWago.sourceM = sourceM;
    }

    public static String getJcM() {
        return jcM;
    }

    public static void setJcM(String jcM) {
        ConfigWago.jcM = jcM;
    }

    public static String getEqptClass() {
        return eqptClass;
    }

    public static void setEqptClass(String eqptClass) {
        ConfigWago.eqptClass = eqptClass;
    }

    public static String getCaCrtFile() {
        return caCrtFile;
    }

    public static void setCaCrtFile(String caCrtFile) {
        ConfigWago.caCrtFile = caCrtFile;
    }

    public static String getClientCrtFile() {
        return clientCrtFile;
    }

    public static void setClientCrtFile(String clientCrtFile) {
        ConfigWago.clientCrtFile = clientCrtFile;
    }

    public static String getClientKeyFile() {
        return clientKeyFile;
    }

    public static void setClientKeyFile(String clientKeyFile) {
        ConfigWago.clientKeyFile = clientKeyFile;
    }

    public static String getSslPassword() {
        return sslPassword;
    }

    public static void setSslPassword(String sslPassword) {
        ConfigWago.sslPassword = sslPassword;
    }

    public static String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public static void setPersistenceUnitName(String persistenceUnitName) {
        ConfigWago.persistenceUnitName = persistenceUnitName;
    }

    public static String getEsbJcAlrmInfo() {
        return esbJcAlrmInfo;
    }

    public static void setEsbJcAlrmInfo(String esbJcAlrmInfo) {
        ConfigWago.esbJcAlrmInfo = esbJcAlrmInfo;
    }

    public static String getEsbJcPmDtecInfo() {
        return esbJcPmDtecInfo;
    }

    public static void setEsbJcPmDtecInfo(String esbJcPmDtecInfo) {
        ConfigWago.esbJcPmDtecInfo = esbJcPmDtecInfo;
    }

    public static String getEsbJcUturnDtecInfo() {
        return esbJcUturnDtecInfo;
    }

    public static void setEsbJcUturnDtecInfo(String esbJcUturnDtecInfo) {
        ConfigWago.esbJcUturnDtecInfo = esbJcUturnDtecInfo;
    }

    
}
