package com.bda.mapreduce.model;

import com.bda.mapreduce.exception.LogParseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogInfo {

    private String host;
    private String time;
    private String logLevel;
    private String httpVerb;
    private String resourcePath;
    private String protocol;
    private String returnCode;
    private String responseLength;


    public LogInfo() {}

    public void parse(String line)
    {

        try {
             String[] splittedLine = line.replace("- ", "")
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(" ");

            this.host = splittedLine[0];
            this.time = splittedLine[1];
            this.logLevel = splittedLine[2];
            this.httpVerb = splittedLine[3];
            this.resourcePath = splittedLine[4];
            this.protocol = splittedLine[5];
            this.returnCode = splittedLine[6];
            this.responseLength = splittedLine[7];

        } catch (RuntimeException e){
            throw new LogParseException(line);
        }
    }

    public String getTimeHour(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(this.time, formatter);

        return dateTime.getHour() + "";
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

    public String getProtocol() { return protocol; }

    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getReturnCode() { return returnCode; }

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
