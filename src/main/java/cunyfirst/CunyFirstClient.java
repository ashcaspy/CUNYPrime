package cunyfirst;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class CunyFirstClient extends WebClient {
    public CunyFirstClient() {
        super(BrowserVersion.CHROME); //silence errors
        getOptions().setCssEnabled(false);
        getOptions().setJavaScriptEnabled(true);
        getCookieManager().setCookiesEnabled(true);
        setIncorrectnessListener(new Silent());

        try {
            request = new WebRequest(
                    new URL(ID.url),
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
        HtmlSelect inst = getSelect(ID.selectSchool);
        inst.setSelectedAttribute(inst.getOptionByText(school), true);
        waitForBackgroundJavaScript(10000);

        HtmlSelect term = getSelect(ID.selectTerm);
        term.setSelectedAttribute(term.getOptionByText(semester), true);
        waitForBackgroundJavaScript(10000);

        //has to be done after school and term are set
        //search for course numbers > 0
        HtmlSelect match = getSelect(ID.matchId);
        match.setSelectedAttribute(match.getOptionByValue(ID.matchValue), true);
        ((HtmlTextInput)searchPage.getElementById(ID.courseNbrId)).setText("0");

        List<NameValuePair> list = searchPage.getForms().get(0).getWebRequest(null).getRequestParameters();

        parameters = new HashMap<>();
        for(NameValuePair p: list) {
            parameters.put(p.getName(), p.getValue());
        }

        parameters.put(ID.submitCode.getName(), ID.submitCode.getValue());
        parameters.put(ID.showClosed.getName(), ID.showClosed.getValue());

    }

    public HtmlPage getResults(String dept) throws IOException {
        parameters.put(ID.deptCode, dept);

        //convert map to list
        List<NameValuePair> newParams = new ArrayList<>();
        parameters.entrySet().stream().map(e -> new NameValuePair(e.getKey(), e.getValue())).forEach(newParams::add);

        request.setRequestParameters(newParams);
        return getPage(request);
    }
}

class Silent implements IncorrectnessListener {
        public void notify(String x, Object y) {}
}
