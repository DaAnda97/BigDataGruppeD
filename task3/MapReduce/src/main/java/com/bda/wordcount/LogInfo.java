package com.bda.wordcount;

public class LogInfo {

    private String host;
    private String time;
    private String logLevel;
    private String httpVerb;
    private String resourcePath;
    private String returnCode;
    private String responseLength;

    public LogInfo() {}

    public LogInfo(
            String host,
            String time,
            String logLevel,
            String httpVerb,
            String resourcePath,
            String returnCode,
            String responseLength)
    {
        this.host = host;
        this.time = time;
        this.logLevel = logLevel;
        this.httpVerb = httpVerb;
        this.resourcePath = resourcePath;
        this.returnCode = returnCode;
        this.responseLength = responseLength;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getHttpVerb() {
        return httpVerb;
    }

    public void setHttpVerb(String httpVerb) {
        this.httpVerb = httpVerb;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getResponseLength() {
        return responseLength;
    }

    public void setResponseLength(String responseLength) {
        this.responseLength = responseLength;
    }

}
