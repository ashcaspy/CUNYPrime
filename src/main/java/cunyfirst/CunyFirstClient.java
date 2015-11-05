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


import java.sql.Connection;
import parser.Parser;


public class CunyFirstClient {
    public CunyFirstClient() {
        client = new WebClient(BrowserVersion.CHROME); //silence errors
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(true);
        client.getCookieManager().setCookiesEnabled(true);
        client.setIncorrectnessListener(new Silent());

        try {
            request = new WebRequest(
                    new URL(ID.url),
                    HttpMethod.POST);
            searchPage = client.getPage(request);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private WebClient client;
    private WebRequest request = null;
    private HtmlPage searchPage = null;
    HashMap<String, String> searchParameters = null;
    HashMap<String, String> sectionRequestParams = null;

    public void retrieve(String college, String season, int year,
                         MatchValuePair courseNumber,
                         TimeRange start, TimeRange end,
                         Iterable<String> departments, Connection db) {
        setup(college, ID.semester(season, year));
        setSearchTerms(courseNumber, start, end);
        for(String dept: departments) {
            try {
                new Parser(this, getResults(dept)).addToTable(db);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public HtmlSelect getSelect(String id) {
        return (HtmlSelect) searchPage.getElementById(id);
    }


    //set institution, term, and course number
    public void setup(String school, String semester) {
        HtmlSelect inst = getSelect(ID.selectSchool);
        inst.setSelectedAttribute(inst.getOptionByText(school), true);
        client.waitForBackgroundJavaScript(10000);

        HtmlSelect term = getSelect(ID.selectTerm);
        term.setSelectedAttribute(term.getOptionByText(semester), true);
        client.waitForBackgroundJavaScript(10000);


        //has to be done after school and term are set

        //only find undergrad courses
        HtmlSelect career = getSelect(ID.selectCareer);
        career.setSelectedAttribute(career.getOptionByText("Undergraduate"), true);

        List<NameValuePair> list = getFormParams(searchPage);
        searchParameters = new HashMap<>(list.size());
        for(NameValuePair p: list) {
            searchParameters.put(p.getName(), p.getValue());
        }

        searchParameters.put(ID.submitCode.getName(), ID.submitCode.getValue());
        searchParameters.put(ID.showClosed.getName(), ID.showClosed.getValue());

    }

    //call after setup
    public void setSearchTerms(MatchValuePair courseNumber, TimeRange start, TimeRange end) {
        if(null != courseNumber) {
            setMatch(ID.matchNbrId, ID.courseNbrId, courseNumber);
        }

        //set times
        if(null != start) {
            searchParameters.put(ID.start, ID.between);
            searchParameters.put(ID.startVal1, Integer.toString(start.min));
            searchParameters.put(ID.startVal2, Integer.toString(start.max));
        }
        if(null != end) {
            searchParameters.put(ID.end, ID.between);
            searchParameters.put(ID.endVal1, Integer.toString(end.min));
            searchParameters.put(ID.endVal2, Integer.toString(end.max));
        }
    }

    //sets one search term (selectId -> pair.comparison, textId -> pair.value)
    void setMatch(String selectId, String textId, MatchValuePair pair) {
        HtmlSelect match = getSelect(selectId);
        match.setSelectedAttribute(match.getOptionByValue(pair.comparison), true);
        ((HtmlTextInput)searchPage.getElementById(textId)).setText(pair.value);
    }

    public HtmlPage getResults(String dept) throws IOException {
        searchParameters.put(ID.deptCode, dept);

        request.setRequestParameters(paramsToList(searchParameters));
        HtmlPage results = client.getPage(request);

        //set up sectionRequestParams
        if(null == sectionRequestParams) {
            List<NameValuePair> secParams = getFormParams(results);
            sectionRequestParams = new HashMap<>(secParams.size());
            for (NameValuePair p : secParams) {
                sectionRequestParams.put(p.getName(), p.getValue());
            }
        }
        //System.out.println("'"+searchParameters.get(ID.endVal1)+"'");
        //System.out.println("'"+searchParameters.get(ID.startVal1)+"'");
        return results;
    }

    public HtmlPage getSection(String sectionNbr) throws IOException {
        sectionRequestParams.put(ID.submitCode.getName(), sectionNbr);
        request.setRequestParameters(paramsToList(sectionRequestParams));
        return client.getPage(request);
    }

    private List<NameValuePair> paramsToList(HashMap<String, String> params) {
        //convert map to list
        List<NameValuePair> newParams = new ArrayList<>();
        params.entrySet().stream().map(e -> new NameValuePair(e.getKey(), e.getValue())).forEach(newParams::add);
        return newParams;
    }

    private List<NameValuePair> getFormParams(HtmlPage page) {
        return page.getForms().get(0).getWebRequest(null).getRequestParameters();
    }

}

class Silent implements IncorrectnessListener {
        public void notify(String x, Object y) {}
}
