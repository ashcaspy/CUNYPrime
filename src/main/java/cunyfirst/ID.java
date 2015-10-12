package cunyfirst;

import com.gargoylesoftware.htmlunit.util.NameValuePair;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;

public class ID {
    public final static String url = "https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/GUEST/HRMS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL";

    //parse search page
    public final static String selectSchool = "CLASS_SRCH_WRK2_INSTITUTION$42$";
    public final static String selectTerm = "CLASS_SRCH_WRK2_STRM$45$";
    public final static String selectDept = "SSR_CLSRCH_WRK_SUBJECT_SRCH$0";

    public final static String matchId = "SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1";
    public final static String matchValue = "G";

    public final static String courseNbrId = "SSR_CLSRCH_WRK_CATALOG_NBR$1";

    public static String semester(String season, int year) {
        return Integer.toString(year) + " " + season + " Term";
    }

    //request parameters
    public final static NameValuePair submitCode = new NameValuePair("ICAction", "CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH");
    public final static NameValuePair showClosed = new NameValuePair("SSR_CLSRCH_WRK_SSR_OPEN_ONLY$chk$5", "N");
    public final static String deptCode = "SSR_CLSRCH_WRK_SUBJECT_SRCH";

    //parse results pages
    private static String searchId(String id) {
        return "[id^="+id+"]";
    }
    public final static String course = searchId("win0divSSR_CLSRSLT_WRK_GROUPBOX2");
    public final static String courseName = searchId("win0divGPSSR_CLSRSLT_WRK_GROUPBOX2");

    public final static String section = searchId("win0divSSR_CLSRCH_MTG");
    public final static String instructor = searchId("MTG_INSTR");
    public final static String sectionTime = searchId("MTG_DAYTIME");
    public final static String sectionRoom = searchId("MTG_ROOM");
    public final static String sectionNbr = searchId("MTG_CLASSNAME");

    public final static String TBA = "TBA";

    //they break database constraints and aren't relevant to scheduling anyway
    public final static List<String> skippedDepts = Arrays.asList(new String[]{"PERM", "STABD"});

    private final static String dayFormat = "\\p{Upper}\\p{Lower}";
    private final static String timeFormat = "\\p{Digit}\\p{Digit}?:\\p{Digit}\\p{Digit}[AP]M";
    public final static String dayTimeRegex = "("+dayFormat+")+ "+timeFormat+" - "+timeFormat;

}
