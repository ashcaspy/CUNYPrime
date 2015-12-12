package search.cunyfirst;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;

import java.util.Map;



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
    private HashMap<String, String> searchParameters = null;
    private HashMap<String, String> resetParameters = null;

    private HashMap<String, String> sectionRequestParams = null;

    
    public HtmlSelect getSelect(String id) {
        return (HtmlSelect) searchPage.getElementById(id);
    }

    public void setSchool(String school) {
        HtmlSelect inst = getSelect(ID.selectSchool);
        inst.setSelectedAttribute(school, true);
        client.waitForBackgroundJavaScript(10000);
    }


    //set institution, term
    //has the effect of resetting all other search terms
    public void setup(String school, String semester) {
        setSchool(school);

        HtmlSelect term = getSelect(ID.selectTerm);
        if(null != semester) {
            term.setSelectedAttribute(term.getOptionByText(semester), true);
        } else {
            // by default choose the latest available term
            term.setSelectedAttribute(term.getOption(term.getOptionSize()-1), true);
        }
        client.waitForBackgroundJavaScript(10000);
        
        HtmlSelect selectCareer = getSelect(ID.selectCareer);
        // if there is only one option for career 
        // (not counting the initial blank one)
        // select it
        if(selectCareer.getOptions().size() == 2) {
            //selectCareer.setSelectedIndex(1);
            selectCareer.setSelectedAttribute(selectCareer.getOption(1), true);
        } else {
        
            try {
                HtmlOption career = selectCareer.getOptionByText("Undergraduate");
                selectCareer.setSelectedAttribute(career, true);
            } catch (ElementNotFoundException | NullPointerException e) {
                // left blank
            }
        }
       
        List<NameValuePair> list = getFormParams(searchPage);
        searchParameters = new HashMap<>(list.size());
        for(NameValuePair p: list) {
            searchParameters.put(p.getName(), p.getValue());
        }

        searchParameters.put(ID.submitCode.getName(), ID.submitCode.getValue());
        searchParameters.put(ID.showClosed.getName(), ID.showClosed.getValue());

        //defaults
        resetParameters = (HashMap<String, String>) searchParameters.clone();
    }

    //reset select search terms
    public void resetTerms(String... keys) {
        for(String k: keys) {
            searchParameters.put(k, resetParameters.get(k));
        }
    }

    //reset all search terms, except for the ones chosen in setup()
    public void resetTerms() {
        for(Map.Entry<String, String> e: resetParameters.entrySet()) {
            searchParameters.put(e.getKey(), e.getValue());
        }
    }

    //call after setup
    public void setSearchTerms(MatchValuePair courseNumber, Integer start, Integer end,
                               String keyword, String professor, int[] days) {
        if(null != courseNumber) {
            searchParameters.put(ID.matchNbrId, courseNumber.comparison);
            searchParameters.put(ID.courseNbrId, courseNumber.value);
        }

        //set times
        if(null != start) {
            searchParameters.put(ID.start, ID.greaterThanEq);
            searchParameters.put(ID.startVal1, ID.toNearTime(start-1));
        }
        if(null != end) {
            searchParameters.put(ID.end, ID.lessThanEq);
            searchParameters.put(ID.endVal1, ID.toNearTime(end));
        }

        if(null != keyword) {
            searchParameters.put(ID.keyword, keyword);
        }

        if(null != professor) {
            searchParameters.put(ID.profMatch, ID.exact);
            searchParameters.put(ID.professor, professor);
        }

        if(null != days) {
            searchParameters.put(ID.whichDays, ID.includeOnly);
            for(int i: days) {
                searchParameters.put(ID.daysOfWeek.get(i), ID.selected);
            }
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
        return getResults();
    }

    //assumes setup and setSearchTerms have been already called
    public HtmlPage getResults() throws IOException {
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
