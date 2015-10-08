//package cunyfirst;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;

class CunyFirstClient extends WebClient {
    public CunyFirstClient() {
        getOptions().setCssEnabled(false);
        getOptions().setJavaScriptEnabled(true);
        getCookieManager().setCookiesEnabled(true);
        setIncorrectnessListener(new Silent());

        try {
            request = new WebRequest(
                    new URL("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/GUEST/HRMS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL"),
                    HttpMethod.POST);
            searchPage = getPage(request);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private WebRequest request = null;
    private HtmlPage searchPage = null;
    HashMap<String, String> parameters = null;

    public HtmlSelect getSelect(String id) {
        return (HtmlSelect) searchPage.getElementById(id);
    }


    //set institution, term, and course number
    public void setup(String school, String semester) {
        HtmlSelect inst = getSelect("CLASS_SRCH_WRK2_INSTITUTION$42$");
        inst.setSelectedAttribute(inst.getOptionByText(school), true);
        waitForBackgroundJavaScript(10000);

        HtmlSelect term = getSelect("CLASS_SRCH_WRK2_STRM$45$");
        term.setSelectedAttribute(term.getOptionByText(semester), true);
        waitForBackgroundJavaScript(10000);

        //has to be done after school and term are set
        //search for course numbers > 0
        HtmlSelect match = getSelect("SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1");
        match.setSelectedAttribute(match.getOptionByValue("G"), true);
        ((HtmlTextInput)searchPage.getElementById("SSR_CLSRCH_WRK_CATALOG_NBR$1")).setText("0");

        List<NameValuePair> list = searchPage.getForms().get(0).getWebRequest(null).getRequestParameters();

        parameters = new HashMap<>();
        for(NameValuePair p: list) {
            parameters.put(p.getName(), p.getValue());
        }

        parameters.put("ICAction", "CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH");
        parameters.put("SSR_CLSRCH_WRK_SSR_OPEN_ONLY$chk$5", "N");

    }

    public HtmlPage getResults(String dept) throws IOException {
        parameters.put("SSR_CLSRCH_WRK_SUBJECT_SRCH", dept);
        List<NameValuePair> newParams = new ArrayList<>();
        parameters.entrySet().stream().map(e -> new NameValuePair(e.getKey(), e.getValue())).forEach(newParams::add);

        request.setRequestParameters(newParams);
        return getPage(request);
    }
}

class Silent implements IncorrectnessListener {
        public void notify(String x, Object y) {}
}
