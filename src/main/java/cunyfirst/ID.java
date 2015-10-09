package cunyfirst;

import com.gargoylesoftware.htmlunit.util.NameValuePair;

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


}
