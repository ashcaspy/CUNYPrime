package search.cunyfirst;

import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.util.*;

public class ID {
    public final static String url = "https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/GUEST/HRMS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL";

    //parse search page
    public final static String selectSchool = "CLASS_SRCH_WRK2_INSTITUTION$42$";
    public final static String selectTerm = "CLASS_SRCH_WRK2_STRM$45$";
    public final static String selectDept = "SSR_CLSRCH_WRK_SUBJECT_SRCH$0";
    public final static String selectCareer = "SSR_CLSRCH_WRK_ACAD_CAREER$2";

    public final static String greaterThan = "G";
    public final static String lessThan = "T";
    public final static String exact = "E";

    public final static String matchNbrId = "SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1";
    public final static String courseNbrId = "SSR_CLSRCH_WRK_CATALOG_NBR$1";

    public final static String between = "BT";
    public final static String greaterThanEq = "GE";
    public final static String lessThanEq = "LE";

    public final static String start = "SSR_CLSRCH_WRK_SSR_START_TIME_OPR$8";
    public final static String startVal1 = "SSR_CLSRCH_WRK_MEETING_TIME_START$8";
    public final static String startVal2 = "SSR_CLSRCH_WRK_SSR_MTGTIME_START2$8";

    public final static String end = "SSR_CLSRCH_WRK_SSR_END_TIME_OPR$8";
    public final static String endVal1 = "SSR_CLSRCH_WRK_MEETING_TIME_END$8";
    public final static String endVal2 = "SSR_CLSRCH_WRK_SSR_MTGTIME_END2$8";

    public final static String keyword = "SSR_CLSRCH_WRK_DESCR$11";

    public final static String profMatch = "SSR_CLSRCH_WRK_SSR_EXACT_MATCH2$16";
    public final static String professor = "SSR_CLSRCH_WRK_LAST_NAME$16";

    public final static String whichDays = "SSR_CLSRCH_WRK_INCLUDE_CLASS_DAYS$9";
    public final static String includeOnly = "I";
    public final static Map<Integer, String> daysOfWeek = Collections.unmodifiableMap(new HashMap<Integer, String>(7) {
        {
            this.put(0, "SSR_CLSRCH_WRK_SUN$chk$9");
            this.put(1, "SSR_CLSRCH_WRK_MON$chk$9");
            this.put(2, "SSR_CLSRCH_WRK_TUES$chk$9");
            this.put(3, "SSR_CLSRCH_WRK_WED$chk$9");
            this.put(4, "SSR_CLSRCH_WRK_THURS$chk$9");
            this.put(5, "SSR_CLSRCH_WRK_FRI$chk$9");
            this.put(6, "SSR_CLSRCH_WRK_SAT$chk$9");
        }
    });

    public final static String selected = "Y";


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

    //they break database constraints and/or aren't relevant to scheduling anyway
    public final static List<String> skippedDepts = Arrays.asList(
            new String[]{"CUNBA", "PERM", "STABD"});

    private final static String dayFormat = "\\p{Upper}\\p{Lower}";
    private final static String timeFormat = "\\p{Digit}\\p{Digit}?:\\p{Digit}\\p{Digit}[AP]M";
    public final static String dayTimeRegex = "("+dayFormat+")+ "+timeFormat+" - "+timeFormat;

    //open or closed
    public final static String openClosed = searchId("win0divDERIVED_CLSRCH_SSR_STATUS_LONG");
    //public final static

    //section page
    public final static String secCourseName = "DERIVED_CLSRCH_DESCR200";
    public final static String courseComponents = "win0divSSR_CLS_DTL_WRK_SSR_COMPONENT_LONG";
    public final static String prereqs = "SSR_CLS_DTL_WRK_SSR_REQUISITE_LONG";
    public final static String courseDescription = "DERIVED_CLSRCH_DESCRLONG";
    public final static String units = "SSR_CLS_DTL_WRK_UNITS_RANGE";
}
