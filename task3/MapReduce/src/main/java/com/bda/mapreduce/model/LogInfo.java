package com.bda.mapreduce.model;

import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LogInfo {

    private String host = "";
    private String time = "";
    private String logLevel = "";
    private String httpVerb = "";
    private String resourcePath = "";
    private String protocol = "";
    private String returnCode = "";
    private String responseLength = "";

    private static Logger logger = Logger.getLogger(LogInfo.class);


    public LogInfo() {
    }

    public void parse(String line) {

        List<String> splittedLine = new ArrayList(Arrays.asList(line.replace("- ", "")
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(" ")));

        //some logs contain protocol, some dont
        if (splittedLine.size() == 7) {
            splittedLine.add(5, "");
        }
        if (splittedLine.size() == 8) {
            this.host = splittedLine.get(0);
            this.time = splittedLine.get(1);
            this.logLevel = splittedLine.get(2);
            this.httpVerb = splittedLine.get(3);
            this.resourcePath = splittedLine.get(4);
            this.protocol = splittedLine.get(5);
            this.returnCode = splittedLine.get(6);
            this.responseLength = splittedLine.get(7);
        } else {
            logger.error("Unable to parse line: " + line);
        }

    }

    public String getTimeHour() {

        if(this.time.equals("")){
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(this.time, formatter);

        return dateTime.getHour() + "";
    }

    public String getTimeDate() {

        if(this.time.equals("")){
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(this.time, formatter);

        String dayAndDate = dateTime.getDayOfWeek().toString() + ", " + dateTime.getDayOfMonth() + ".";

        return dayAndDate;
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
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
