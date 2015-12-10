package search;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import search.cunyfirst.CunyFirstClient;
import search.cunyfirst.ID;
import search.cunyfirst.MatchValuePair;
import search.parser.SearchError;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.stream.Collectors;
import search.parser.Parser;
import search.parser.Section;

// find sections using cunyfirst
public class CunyFirstSearch extends Search {
    private CunyFirstClient client = new CunyFirstClient();

    protected CunyFirstSearch(Connection c, int id) {
        super(c, id);
    }

    protected CunyFirstSearch(Connection c, int id, String college, String semester) {
        this(c, id);
        selectTerm(college, semester);
    }

    public List<String> getSchools() {
        return client.getSelect(ID.selectSchool).getOptions().stream().map(HtmlOption::getText).collect(Collectors.toList());
    }

    public List<String> getSemesters() {
        return client.getSelect(ID.selectTerm).getOptions().stream().map(HtmlOption::getText).collect(Collectors.toList());
    }

    public void selectTerm(String school, String semester) {
        client.setup(school, semester);
    }

    public void find(MatchValuePair courseNumber,
                    Integer start, Integer end,
                    String keyword, String professor,
                    int[] days, List<String> departments,
                    boolean[] errors) {

        //clear previous search parameters in case some of these are null
        client.resetTerms();
        
        if(null == courseNumber) {
            // default to this criteria, which doesn't affect results 
            // ensures at least one was picked
            courseNumber = new MatchValuePair(ID.greaterThan, "0");
        }

        client.setSearchTerms(courseNumber, start, end, keyword, professor, days);

        try {
            Section.createTable(conn, tableName());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        if(null != departments) {
            for (String dept : departments) {
                try {
                    new Parser(client.getResults(dept)).addToTable(conn, tableName());
                    ErrorCode.SUCCESS.setArray(errors);
                } catch (IOException e) {
                } catch (SearchError e) {
                    ErrorCode.fromMsg(e.msg).setArray(errors);
                }
            }
        }
        else {
            try {
                new Parser(client.getResults()).addToTable(conn, tableName());
                ErrorCode.SUCCESS.setArray(errors);
            } catch (IOException e) { }
              catch (SearchError e) {
                  ErrorCode.fromMsg(e.msg).setArray(errors);
              }
        }
        /*
        ++counter;
        if(counter > 2) {
            counter = 1;
        }
        */
    }

}
