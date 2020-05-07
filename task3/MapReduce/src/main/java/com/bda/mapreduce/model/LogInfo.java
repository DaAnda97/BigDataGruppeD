package com.bda.mapreduce.model;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogInfo {

    private String host = "";
    private String time = "";
    private String returnCode = "";
    private String responseLength = "";

    private static final Logger logger = LogManager.getLogger(LogInfo.class);
    private static final Pattern LOG_PATTERN = Pattern.compile("^(\\S+) - - \\[(\\S+) -\\S+] \".* (\\S+) (\\S+)$");

    public LogInfo() {
    }

    public LogInfo(String host, String time, String returnCode, String responseLength) {
        this.host = host;
        this.time = time;
        this.returnCode = returnCode;
        this.responseLength = responseLength;
    }

    public LogInfo parse(String line) {

        Matcher logMatcher = LOG_PATTERN.matcher(line);

        if (logMatcher.find()) {
            // group(0) contains the whole line
            String host = logMatcher.group(1);
            String time = logMatcher.group(2);
            String returnCode = logMatcher.group(3);
            String responseLength = logMatcher.group(4);

            return new LogInfo(host, time, returnCode, responseLength );
        }
        else {
            logger.error("Unable to parse line: " + line);
            return this;
        }

    }

    public String getTimeHour() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(this.time, formatter);

        return dateTime.getHour() + "";
    }

    public String getTimeDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(this.time, formatter);

        String dayAndDate = dateTime.getDayOfMonth() + " (" + dateTime.getDayOfWeek().toString().toLowerCase().substring(0,2) + ")";

        return dayAndDate;
    }

    public String getHost() {
        return host;
    }

    public String getTime() {
        return time;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public String getResponseLength() {
        return responseLength;
    }
}
