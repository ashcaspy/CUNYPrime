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


/**
 * For running searches on the publicly available class schedules
 * @author Kat
 */
public class CunyFirstClient {
    public CunyFirstClient() {
        //silence errors
        client = new WebClient(BrowserVersion.CHROME); 
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(true);
        client.getCookieManager().setCookiesEnabled(true);
        client.setIncorrectnessListener(new Silent());

        // create the connection
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
    // a copy of searchParameters when it is made, so params can be reset later
    private HashMap<String, String> resetParameters = null;
    
    // for loading course/section info after getting section results
    private HashMap<String, String> sectionRequestParams = null;

    /**
     * 
     * @param id the id of the select element
     * @return the specified element cast as HtmlSelect
     */
    public HtmlSelect getSelect(String id) {
        return (HtmlSelect) searchPage.getElementById(id);
    }

    /**
     * Sets the school by itself
     * waits for all other options to be updated before returning
     * @param school the school id
     */
    public void setSchool(String school) {
        HtmlSelect inst = getSelect(ID.selectSchool);
        inst.setSelectedAttribute(school, true);
        client.waitForBackgroundJavaScript(10000);
    }


    /**
     * Sets the initial search options: school, semester.
     * Resets searchParameters.
     * Defaults to Undergraduate where possible.
     * @param school the school id
     * @param semester the text representing the term ("year season Term"), if null will choose the last and presumably latest term
     */
    public void setup(String school, String semester) {
        // set school
        setSchool(school);

        // set term
        HtmlSelect term = getSelect(ID.selectTerm);
        if(null != semester) {
            term.setSelectedAttribute(term.getOptionByText(semester), true);
        } else {
            // by default choose the latest available term
            term.setSelectedAttribute(term.getOption(term.getOptionSize()-1), true);
        }
        client.waitForBackgroundJavaScript(10000);
        
        // set career if there is only one option OR "Undergraduate" is valid
        // for at least one school neither of these is applicable
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
       
        // set searchParameters
        List<NameValuePair> list = getFormParams(searchPage);
        searchParameters = new HashMap<>(list.size());
        for(NameValuePair p: list) {
            searchParameters.put(p.getName(), p.getValue());
        }

        // set the code needed to submit the form
        searchParameters.put(ID.submitCode.getName(), ID.submitCode.getValue());
        
        // show closed classes
        searchParameters.put(ID.showClosed.getName(), ID.showClosed.getValue());

        //defaults
        resetParameters = (HashMap<String, String>) searchParameters.clone();
    }

    /**
     * reset the named search parameters
     * @param keys 
     */
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

    /**
     * sets non-null search parameters - only call after setup()
     * time parameters will be offset by a half hour
     * @param courseNumber courseNumber a (comparison, text) pair to check against course number, defaults to >='0' which should get everything
     * @param start earliest start hour
     * @param end earlier end hour
     * @param keyword keyword option
     * @param professor professor option, uses contains
     * @param days the days to select. "Include only" gets classes that meet on ALL of these days but not necessarily ONLY these days
     */
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
            searchParameters.put(ID.profMatch, ID.contains);
            searchParameters.put(ID.professor, professor);
        }

        if(null != days) {
            searchParameters.put(ID.whichDays, ID.includeOnly);
            for(int i: days) {
                searchParameters.put(ID.daysOfWeek.get(i), ID.selected);
            }
        }
    }

    /**
     * sets one two-part search term (selectId -> pair.comparison, textId -> pair.value)
     * @param selectId the id of the select element
     * @param textId the id of the text box
     * @param pair pair.comparison sets the select, pair.value sets the text
     */
    void setMatch(String selectId, String textId, MatchValuePair pair) {
        HtmlSelect match = getSelect(selectId);
        match.setSelectedAttribute(match.getOptionByValue(pair.comparison), true);
        ((HtmlTextInput)searchPage.getElementById(textId)).setText(pair.value);
    }

    /**
     * 
     * @param dept the department to search for
     * @return the results page of all current search terms
     * @throws IOException 
     */
    public HtmlPage getResults(String dept) throws IOException {
        searchParameters.put(ID.deptCode, dept);
        HtmlPage results = getResults();
        // make sure dept is cleared afterwards
        resetTerms(ID.deptCode);
        return results;
    }

    /**
     * Runs a search and returns the results. 
     * assumes setup and setSearchTerms have been already called
     * @return the results page of all current search terms
     * @throws IOException 
     */
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

    /**
     * load course/section info page (contains enrollment info we don't use and course info that we do use)
     * @param sectionNbr the id of the element containing the section number
     * @return the section page
     * @throws IOException 
     */
    public HtmlPage getSection(String sectionNbr) throws IOException {
        sectionRequestParams.put(ID.submitCode.getName(), sectionNbr);
        request.setRequestParameters(paramsToList(sectionRequestParams));
        return client.getPage(request);
    }

    /**
     * converts a map to a list of NameValuePair
     * @param params a map of String pairs
     * @return params.toList.map(new NameValuePair(_,_))
     */
    private List<NameValuePair> paramsToList(HashMap<String, String> params) {
        //convert map to list
        List<NameValuePair> newParams = new ArrayList<>();
        params.entrySet().stream().map(e -> new NameValuePair(e.getKey(), e.getValue())).forEach(newParams::add);
        return newParams;
    }

    /**
     * Get request parameters from the first form on page. Works for both searchPage and results pages
     * @param page the page containing the form
     * @return request parameters
     */
    private List<NameValuePair> getFormParams(HtmlPage page) {
        return page.getForms().get(0).getWebRequest(null).getRequestParameters();
    }

}

class Silent implements IncorrectnessListener {
        public void notify(String x, Object y) {}
}
