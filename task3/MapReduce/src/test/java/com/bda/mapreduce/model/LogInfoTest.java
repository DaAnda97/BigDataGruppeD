package com.bda.mapreduce.model;

import com.bda.mapreduce.exception.LogParseException;
import org.junit.Test;

import static org.junit.Assert.*;

public class LogInfoTest {

    private static final String LOG = "199.72.81.55 - - [01/Jul/1995:00:00:01 -0400] \"GET /history/apollo/ HTTP/1.0\" 200 6245";
    private static final String LOG_2 = "waters-gw.starway.net.au - - [01/Jul/1995:00:00:25 -0400] \"GET /shuttle/missions/51-l/mission-51-l.html HTTP/1.0\" 200 6723";
    private static final String LOG_3 = "pipe6.nyc.pipeline.com - - [01/Jul/1995:00:22:43 -0400] \"GET /shuttle/missions/sts-71/movies/sts-71-mir-dock.mpg\" 200 946425";

    private static final String CORRUPT_LOG = "[01/Jul/1995:00:00:01 -0400] \"GET /history/apollo/ HTTP/1.0\" 200 6245";

    
    @Test
    public void shouldDetectHours() {
        LogInfo info = new LogInfo();
        info.parse(LOG);

        assertEquals("0", info.getTimeHour());
    }

    @Test(expected = LogParseException.class)
    public void shouldNotBeAbleToParse() {

        try
        {
            LogInfo info = new LogInfo();
            info.parse(CORRUPT_LOG);
        }
        catch(LogParseException e)
        {
            String message = "Error while parsing Line: " + CORRUPT_LOG;
            assertEquals(message, e.getMessage());
            throw e;
        }
        fail("LogParseException exception did not throw!");

    }

    @Test
    public void shouldParseLog3Correctly() {
        LogInfo info = new LogInfo();
        info.parse(LOG_3);

        assertEquals("pipe6.nyc.pipeline.com", info.getHost());
        assertEquals("01/Jul/1995:00:22:43", info.getTime());
        assertEquals("-0400", info.getLogLevel());
        assertEquals("GET", info.getHttpVerb());
        assertEquals("/shuttle/missions/sts-71/movies/sts-71-mir-dock.mpg", info.getResourcePath());
        assertEquals("", info.getProtocol());
        assertEquals("200", info.getReturnCode());
        assertEquals("946425", info.getResponseLength());
    }

    @Test
    public void shouldParseLog2Correctly() {
        LogInfo info = new LogInfo();
        info.parse(LOG_2);

        assertEquals("waters-gw.starway.net.au", info.getHost());
        assertEquals("01/Jul/1995:00:00:25", info.getTime());
        assertEquals("-0400", info.getLogLevel());
        assertEquals("GET", info.getHttpVerb());
        assertEquals("/shuttle/missions/51-l/mission-51-l.html", info.getResourcePath());
        assertEquals("HTTP/1.0", info.getProtocol());
        assertEquals("200", info.getReturnCode());
        assertEquals("6723", info.getResponseLength());
    }

    @Test
    public void shouldParseLogCorrectly() {
        LogInfo info = new LogInfo();
        info.parse(LOG);

        assertEquals("199.72.81.55", info.getHost());
        assertEquals("01/Jul/1995:00:00:01", info.getTime());
        assertEquals("-0400", info.getLogLevel());
        assertEquals("GET", info.getHttpVerb());
        assertEquals("/history/apollo/", info.getResourcePath());
        assertEquals("HTTP/1.0", info.getProtocol());
        assertEquals("200", info.getReturnCode());
        assertEquals("6245", info.getResponseLength());
    }

}
