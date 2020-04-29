import com.bda.wordcount.LogInfo;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

public class StringParserTest {

    private static final String LOG = "199.72.81.55 - - [01/Jul/1995:00:00:01 -0400] \"GET /history/apollo/ HTTP/1.0\" 200 6245";
    private static final String LOG_2 = "waters-gw.starway.net.au - - [01/Jul/1995:00:00:25 -0400] \"GET /shuttle/missions/51-l/mission-51-l.html HTTP/1.0\" 200 6723";

    @Test
    public void test() throws ParseException {
        String[] newString = LOG_2.replace("- ", "")
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(" ");

        LogInfo info =
                new LogInfo(newString[0], newString[1], newString[2], newString[3], newString[4], newString[5], newString[6]);

        String unformattedTime = info.getTime();
        SimpleDateFormat parser = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
        Date date = parser.parse(unformattedTime);
        int hour = date.toInstant().atZone(ZoneId.systemDefault()).getHour();
        System.out.println(hour);
    }

}
