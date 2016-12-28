package teamcity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by jozef on 27.12.2016.
 */
public class FogBugzEventData {
    private String token;
    private String sAction;
    private String sEventType;
    private String ixBug;
    private DateTime dtEventUtc;
    private String sPersonName;
    private String sMessage;
    private String sExternalUrl;
    private String sModuleName;
    private String sBranchName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAction() {
        return sAction;
    }

    public void setAction(String sAction) {
        this.sAction = sAction;
    }

    public String getEventType() {
        return sEventType;
    }

    public void setEventType(String sEventType) {
        this.sEventType = sEventType;
    }

    public String getBug() {
        return ixBug;
    }

    public void setBug(String ixBug) {
        this.ixBug = ixBug;
    }

    public DateTime getEventUtc() {
        return dtEventUtc;
    }

    public void setEventUtc(DateTime dtEventUtc) {
        this.dtEventUtc = dtEventUtc;
    }

    public String getPersonName() {
        return sPersonName;
    }

    public void setPersonName(String sPersonName) {
        this.sPersonName = sPersonName;
    }

    public String getMessage() {
        return sMessage;
    }

    public void setMessage(String sMessage) {
        this.sMessage = sMessage;
    }

    public String getExternalUrl() {
        return sExternalUrl;
    }

    public void setExternalUrl(String sExternalUrl) {
        this.sExternalUrl = sExternalUrl;
    }

    public String getModuleName() {
        return sModuleName;
    }

    public void setModuleName(String sModuleName) {
        this.sModuleName = sModuleName;
    }

    public String getBranchName() {
        return sBranchName;
    }

    public void setBranchName(String sBranchName) {
        this.sBranchName = sBranchName;
    }

    public List<NameValuePair> toParams() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("token", this.token));
        params.add(new BasicNameValuePair("sAction", this.sAction));
        params.add(new BasicNameValuePair("sEventType", this.sEventType));
        params.add(new BasicNameValuePair("ixBug", this.ixBug));
        params.add(new BasicNameValuePair("dtEventUtc", formatToUtcDate(this.dtEventUtc)));
        params.add(new BasicNameValuePair("sPersonName", this.sPersonName));
        params.add(new BasicNameValuePair("sMessage", this.sMessage));
        params.add(new BasicNameValuePair("sExternalUrl", this.sExternalUrl));
        params.add(new BasicNameValuePair("sModuleName", this.sModuleName));
        params.add(new BasicNameValuePair("sBranchName", this.sBranchName));

        return params;
    }

    private static String formatToUtcDate(DateTime date) {
        date = date.toDateTime(DateTimeZone.UTC);
        String formatted = date.toString("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return formatted;
    }
}
