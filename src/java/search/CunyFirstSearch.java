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

/** 
 * search for sections using CUNYirst
 * 
 * @author Kat
 */
public class CunyFirstSearch extends Search {
    // the connection to CUNYFirst
    private CunyFirstClient client = new CunyFirstClient();

    protected CunyFirstSearch(Connection c, int id) {
        super(c, id);
    }

    /**
     * initialize and select school and term
     * @param c the database connection to use
     * @param id the Search id to create a unique table
     * @param college forwarded to selectTerm
     * @param semester forwarded to selectTerm
     */
    protected CunyFirstSearch(Connection c, int id, String college, String semester) {
        this(c, id);
        selectTerm(college, semester);
    }

    /**
     * Sets the school and semester, which are required for accessing everything else
     * @param school the school id
     * @param semester the semester (by text)
     */
    public void selectTerm(String school, String semester) {
        client.setup(school, semester);
    }

    /**
     * Runs a search with these parameters, adds the results to the table sections_#
     * Any set to null will be ignored
     * @param courseNumber a (comparison, text) pair to check against course number, defaults to >='0' which should get everything
     * @param start minimum start time (an hour between 0 and 23)
     * @param end maximum end time
     * @param keyword keyword option
     * @param professor professor option, uses contains
     * @param days the days to select. Will get classes that meet on ALL of these days but not necessarily ONLY these days
     * @param departments a list of departments to check - will run one search for EACH department
     * @param errors - a boolean array that gets set to true if a result happens. More than one may be set. {SUCCESS, NORESULTS, TOOMANY}
     */
    public void find(MatchValuePair courseNumber,
                    Integer start, Integer end,
                    String keyword, String professor,
                    int[] days, List<String> departments,
                    boolean[] errors) {

        //clear previous search parameters 
        //in case some of the new ones are null and should be cleared
        client.resetTerms();
        
        if(null == courseNumber) {
            // default to this criteria, which doesn't affect results 
            // ensures at least one was picked
            courseNumber = new MatchValuePair(ID.greaterThan, "0");
        }
        
        // add an additional criteria that doesn't filter anything
        // since for some schools (I think just the Grad Center now, 
        // since it has neither "Undergraduate" not only one option)
        // the career option isn't selected
        // and error-handling assumes that MORECRITERIA will never happen
        // use 1 since client subtracts a half hour from the start time
        // I don't think anyone cares about classes that start before 12:30AM
        if(null == start) {
            start = 1;
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
