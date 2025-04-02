package module.message;

import com.google.gson.annotations.SerializedName;

public class MsgHeader {
    @SerializedName("source_m")
    private String sourceM;
    @SerializedName("event_dt")
    private String eventDt;
    @SerializedName("event_id")
    private String eventId;
    @SerializedName("trans_id")
    private String transId;
    @SerializedName("error_c")
    private String errorC;
    @SerializedName("error_txt")
    private String errorTxt;

    public String getSourceM() {
        return sourceM;
    }

    public void setSourceM(String sourceM) {
        this.sourceM = sourceM;
    }

    public String getEventDt() {
        return eventDt;
    }

    public void setEventDt(String eventDt) {
        this.eventDt = eventDt;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getErrorC() {
        return errorC;
    }

    public void setErrorC(String errorC) {
        this.errorC = errorC;
    }

    public String getErrorTxt() {
        return errorTxt;
    }

    public void setErrorTxt(String errorTxt) {
        this.errorTxt = errorTxt;
    }
}
