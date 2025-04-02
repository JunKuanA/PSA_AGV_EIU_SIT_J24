package module.config;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigSetup {
    private static final Logger logger = Logger.getLogger(ConfigSetup.class.getName());
    private static final String DEFAULT_CONFIGURATION_FILENAME = "./aciconfig.properties";
    private static Properties prop;

    private ConfigSetup() {
    }

    public static void setup() {
        initSetup();
    }

    private static void initSetup() {
        try (FileInputStream ip = new FileInputStream(DEFAULT_CONFIGURATION_FILENAME)) {
            prop = new Properties();
            prop.load(ip);

            configWago();
            configDatasource();
            configMessage();
        } catch (Exception ex) {
            logger.error("ConfigSetup - initSetup", ex);
        }
    }

    private static void configWago() {
        try {
            ConfigWago.setEsbJcUpdSt(prop.getProperty("ESB_JC_UPD_ST").toUpperCase());
            ConfigWago.setEsbJcUpdStR(prop.getProperty("ESB_JC_UPD_ST_R").toUpperCase());
            ConfigWago.setEsbJcEnqInfo(prop.getProperty("ESB_JC_ENQ_INFO").toUpperCase());
            ConfigWago.setEsbJcEnqInfoR(prop.getProperty("ESB_JC_ENQ_INFO_R").toUpperCase());
            ConfigWago.setEsbJcRstAlrm(prop.getProperty("ESB_JC_RST_ALRM").toUpperCase());
            ConfigWago.setEsbJcRstAlrmR(prop.getProperty("ESB_JC_RST_ALRM_R").toUpperCase());
            ConfigWago.setEsbJcAckR(prop.getProperty("ESB_JC_ACK_R").toUpperCase());
            ConfigWago.setEsbJcHrtbtInfo(prop.getProperty("ESB_JC_HRTBT_INFO").toUpperCase());
            ConfigWago.setEsbJcAlrmInfo(prop.getProperty("ESB_JC_ALRM_INFO").toUpperCase());
            ConfigWago.setEsbJcPmDtecInfo(prop.getProperty("ESB_JC_PM_DTEC_INFO").toUpperCase());
            ConfigWago.setEsbJcUturnDtecInfo(prop.getProperty("ESB_JC_UTURN_DTEC_INFO").toUpperCase());
            ConfigWago.setEsbTjChgModeR(prop.getProperty("ESB_TJ_CHG_MODE_R").toUpperCase());

            ConfigWago.setOPCUAUrl(prop.getProperty("OPCUA_URL"));
            ConfigWago.setMqttBrokerUser(prop.getProperty("MQTT_WAGO_USER"));
            ConfigWago.setMqttBrokerPassword(prop.getProperty("MQTT_WAGO_PASSWORD"));
            ConfigWago.setKeepAliveInterval(Integer.parseInt(prop.getProperty("MQTT_WAGO_KEEP_ALIVE_INTERVAL")));
            ConfigWago.setCleanSession(Integer.parseInt(prop.getProperty("MQTT_WAGO_CLEAN_SESSION")));
            ConfigWago.setMaxInFlight(Integer.parseInt(prop.getProperty("MQTT_WAGO_MAX_INFLIGHT")));
            ConfigWago.setQos(Integer.parseInt(prop.getProperty("MQTT_WAGO_QOS")));

            ConfigWago.setGateType(prop.getProperty("MQTT_WAGO_TYPE").toUpperCase());
            ConfigWago.setMqttPublisherClientId(prop.getProperty("MQTT_WAGO_HB_PUB_CLIENT_ID"));
            ConfigWago.setMqttSubscriberClientId(prop.getProperty("MQTT_WAGO_HB_SUB_CLIENT_ID"));
            ConfigWago.setMqttProcessClientId(prop.getProperty("MQTT_WAGO_THREAD_SUB_CLIENT_ID"));
            ConfigWago.setPublishHeartbeatTopic(prop.getProperty("MQTT_WAGO_HB_PUB_TOPIC"));
            ConfigWago.setSubscribeHeartbeatTopic(prop.getProperty("MQTT_WAGO_HB_SUB_TOPIC"));
            ConfigWago.setSubscribeInfoTopic(prop.getProperty("MQTT_WAGO_INFO_TOPIC"));
            ConfigWago.setPublishGateTopic(prop.getProperty("MQTT_WAGO_PUB_TOPIC"));
            ConfigWago.setSubscribeGateTopic(prop.getProperty("MQTT_WAGO_SUB_GATE_TOPIC"));
            ConfigWago.setSubscribeLightTopic(prop.getProperty("MQTT_WAGO_SUB_LIGHT_TOPIC"));
            ConfigWago.setTriggerCheck(Integer.parseInt(prop.getProperty("MQTT_WAGO_HB_TRIGGER_CHECK")));
            ConfigWago.setMaxHeartbeatTolerance(Integer.parseInt(prop.getProperty("MQTT_WAGO_HB_MAX_TOLERANCE")));
            ConfigWago.setInfoTolerance(Integer.parseInt(prop.getProperty("MQTT_WAGO_HB_INFO_TOLERANCE")));
            ConfigWago.setThreadTimeout(Integer.parseInt(prop.getProperty("MQTT_WAGO_THREAD_TIMEOUT")));
            ConfigWago.setDeviceList(new ArrayList<>(
                    Arrays.asList(prop.getProperty("MQTT_WAGO_DEVICE_LIST").replaceAll("\\s", "").split(","))));

            ConfigWago.setMessageCheckerEnabled(Integer.parseInt(prop.getProperty("MSG_MESSAGE_CHECKER_ENABLED")));
            ConfigWago.setMessageCheckerInterval(Integer.parseInt(prop.getProperty("MSG_MESSAGE_CHECKER_INTERVAL")));
            ConfigWago.setMessageRetries(Integer.parseInt(prop.getProperty("MSG_MESSAGE_RETRIES")));
            ConfigWago.setAckTolerance(Integer.parseInt(prop.getProperty("MSG_ACK_TOLERANCE")));
            ConfigWago.setIgnoreTolerance(Integer.parseInt(prop.getProperty("MSG_IGNORE_TOLERANCE")));

            ConfigWago.setCaCrtFile(prop.getProperty("SSL_CACRTFILE_LOCATION"));
            ConfigWago.setClientCrtFile(prop.getProperty("SSL_CLIENTCRTFILE_lOCATION"));
            ConfigWago.setClientKeyFile(prop.getProperty("SSL_CLIENTKEYFILE_LOCATION"));
            ConfigWago.setSslPassword(prop.getProperty("SSL_PASSWORD"));
        } catch (Exception ex) {
            logger.error("ConfigSetup - configWago", ex);
        }
    }

    private static void configDatasource() {
        try {
            ConfigWago.setPersistenceUnitName(prop.getProperty("EMF_ENT_FAC"));
        } catch (Exception ex) {
            logger.error("ConfigSetup - configDatasource", ex);
        }
    }

    private static void configMessage() {
        try {
            ConfigWago.setSourceM(prop.getProperty("MQTT_MESSAGE_SOURCE_M"));
            ConfigWago.setJcM(prop.getProperty("MQTT_MESSAGE_JC_M"));
            ConfigWago.setJcId(Integer.parseInt(prop.getProperty("MQTT_MESSAGE_JC_ID")));
            ConfigWago.setEqptClass(prop.getProperty("MQTT_MESSAGE_EQPT_CLASS"));
        } catch (Exception ex) {
            logger.error("ConfigSetup - configMessage", ex);
        }
    }
}
