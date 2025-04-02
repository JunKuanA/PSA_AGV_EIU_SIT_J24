package module.process;

import java.util.List;

import com.psa.gtosplus.esb.client.ESBClient;

import module.message.*;

public class Wago {

    private int deviceId = 0;
    private String errorC = "";
    // For JcUpdSt
    private String cmdType = "";
    private int wagoGateCmd = 0;
    private int wagoGateStatus = 0;
    private int wagoStatusCode = 0;
    private String wagoStatusCodeMssg = "";
    private String wagoStatusDesc = "";
    private String newJcSt = "";
    private String curJcSt = "";
    private String jcRst = "";
    private String controlMode = "";
    private String plcLost = "";
    private String alertMode = "";
    private int numOfLoop = 0;
    private List<?> pmLoopList = null;
    private int numOfUturn = 0;
    private List<?> uturnLoopList = null;
    private int numOfFault = 0;
    private List<?> faultCodeDescList = null;
    private String transId ="";

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    private ESBClient esbClient;
    private JcUpdSt jcSt;
    private JcAckR jcAckR;
    private JcRstAlrm jcRstAlrm;
    private JcHrtbtInfo jcHrtbtInfo;
    private JcUturnDtecInfo jcUturnDtecInfo;
    private JcPmDtecInfo jcPmDtecInfo;

    private JcAlrmInfo jcAlrmInfo;
    private JcEnqInfo jcEnqInfo;
    private TjChgModeR tjChgModeR;
    private String currTjMode;
    private String newTjMode = "";

    public Wago() {
        //
    }
    public String getCurJcSt() {
        return curJcSt;
    }

    public void setCurJcSt(String curJcSt) {
        this.curJcSt = curJcSt;
    }
    public JcPmDtecInfo getJcPmDtecInfo() {
        return jcPmDtecInfo;
    }

    public void setJcPmDtecInfo(JcPmDtecInfo jcPmDtecInfo) {
        this.jcPmDtecInfo = jcPmDtecInfo;
    }
    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getErrorC() {
        return errorC;
    }

    public void setErrorC(String errorC) {
        this.errorC = errorC;
    }

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    public int getWagoGateCmd() {
        return wagoGateCmd;
    }

    public void setWagoGateCmd(int wagoGateCmd) {
        this.wagoGateCmd = wagoGateCmd;
    }

    public int getWagoGateStatus() {
        return wagoGateStatus;
    }

    public void setWagoGateStatus(int wagoGateStatus) {
        this.wagoGateStatus = wagoGateStatus;
    }

    public int getWagoStatusCode() {
        return wagoStatusCode;
    }

    public void setWagoStatusCode(int wagoStatusCode) {
        this.wagoStatusCode = wagoStatusCode;
    }

    public String getWagoStatusCodeMssg() {
        return wagoStatusCodeMssg;
    }

    public void setWagoStatusCodeMssg(String wagoStatusCodeMssg) {
        this.wagoStatusCodeMssg = wagoStatusCodeMssg;
    }

    public String getWagoStatusDesc() {
        return wagoStatusDesc;
    }

    public void setWagoStatusDesc(String wagoStatusDesc) {
        this.wagoStatusDesc = wagoStatusDesc;
    }

    public String getNewJcSt() {
        return newJcSt;
    }

    public void setNewJcSt(String newJcSt) {
        this.newJcSt = newJcSt;
    }

    public String getJcRst() {
        return jcRst;
    }

    public void setJcRst(String jcRst) {
        this.jcRst = jcRst;
    }

    public String getControlMode() {
        return controlMode;
    }

    public void setControlMode(String controlMode) {
        this.controlMode = controlMode;
    }

    public String getPlcLost() {
        return plcLost;
    }

    public void setPlcLost(String plcLost) {
        this.plcLost = plcLost;
    }

    public String getAlertMode() {
        return alertMode;
    }

    public void setAlertMode(String alertMode) {
        this.alertMode = alertMode;
    }

    public int getNumOfLoop() {
        return numOfLoop;
    }

    public void setNumOfLoop(int numOfLoop) {
        this.numOfLoop = numOfLoop;
    }

    public List<?> getPmLoopList() {
        return pmLoopList;
    }

    public void setPmLoopList(List<?> pmLoopList) {
        this.pmLoopList = pmLoopList;
    }

    public int getNumOfUturn() {
        return numOfUturn;
    }

    public void setNumOfUturn(int numOfUturn) {
        this.numOfUturn = numOfUturn;
    }

    public List<?> getUturnLoopList() {
        return uturnLoopList;
    }

    public void setUturnLoopList(List<?> uturnLoopList) {
        this.uturnLoopList = uturnLoopList;
    }

    public int getNumOfFault() {
        return numOfFault;
    }

    public void setNumOfFault(int numOfFault) {
        this.numOfFault = numOfFault;
    }

    public List<?> getFaultCodeDescList() {
        return faultCodeDescList;
    }

    public void setFaultCodeDescList(List<?> faultCodeDescList) {
        this.faultCodeDescList = faultCodeDescList;
    }

    public ESBClient getEsbClient() {
        return esbClient;
    }

    public void setEsbClient(ESBClient esbClient) {
        this.esbClient = esbClient;
    }

    public JcUpdSt getJcSt() {
        return jcSt;
    }

    public void setJcSt(JcUpdSt jcSt) {
        this.jcSt = jcSt;
    }

    public JcAckR getJcAckR() {
        return jcAckR;
    }

    public void setJcAckR(JcAckR jcAckR) {
        this.jcAckR = jcAckR;
    }

    public JcRstAlrm getJcRstAlrm() {
        return jcRstAlrm;
    }

    public void setJcRstAlrm(JcRstAlrm jcRstAlrm) {
        this.jcRstAlrm = jcRstAlrm;
    }

    public JcHrtbtInfo getJcHrtbtInfo() {
        return jcHrtbtInfo;
    }

    public void setJcHrtbtInfo(JcHrtbtInfo jcHrtbtInfo) {
        this.jcHrtbtInfo = jcHrtbtInfo;
    }

    public JcUturnDtecInfo getJcUturnDtecInfo() {
        return jcUturnDtecInfo;
    }

    public void setJcUturnDtecInfo(JcUturnDtecInfo jcUturnDtecInfo) {
        this.jcUturnDtecInfo = jcUturnDtecInfo;
    }

    public JcAlrmInfo getJcAlrmInfo() {
        return jcAlrmInfo;
    }

    public void setJcAlrmInfo(JcAlrmInfo jcAlrmInfo) {
        this.jcAlrmInfo = jcAlrmInfo;
    }

    public JcEnqInfo getJcEnqInfo() {
        return jcEnqInfo;
    }

    public void setJcEnqInfo(JcEnqInfo jcEnqInfo) {
        this.jcEnqInfo = jcEnqInfo;
    }

    public TjChgModeR getTjChgModeR() {
        return tjChgModeR;
    }

    public void setTjChgModeR(TjChgModeR tjChgModeR) {
        this.tjChgModeR = tjChgModeR;
    }

    public String getCurrTjMode() {
        return currTjMode;
    }

    public void setCurrTjMode(String currTjMode) {
        this.currTjMode = currTjMode;
    }

    public String getNewTjMode() {
        return newTjMode;
    }

    public void setNewTjMode(String newTjMode) {
        this.newTjMode = newTjMode;
    }

}
