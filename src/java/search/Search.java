package search;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import search.cunyfirst.CunyFirstClient;
import search.cunyfirst.ID;
import search.cunyfirst.MatchValuePair;
import search.cunyfirst.TimeRange;
import parser.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import search.parser.Course;
import search.parser.Parser;
import search.parser.Section;

public class Search {
    private CunyFirstClient client = new CunyFirstClient();
    private final Connection conn;

    //create separate tables for each search run
    private int counter = 1;

    public Search(Connection c) {
        conn = c;
    }

    public Search(Connection c, String college, String semester) {
        conn = c;
        selectTerm(college, semester);
    }

    public List<String> getSchools() {
        return client.getSelect(ID.selectSchool).getOptions().stream().map(HtmlOption::getText).collect(Collectors.toList());
    }

    //assume setup was called, otherwise returns empty list
    public List<String> getDepts() {
        return client.getSelect(ID.selectDept).getOptions().stream().map(HtmlOption::getValueAttribute).collect(Collectors.toList());
    }

    public void selectTerm(String school, String semester) {
        client.setup(school, semester);
    }

    public void find(MatchValuePair courseNumber,
                    TimeRange start, TimeRange end,
                    String keyword, String professor,
                    int[] days, Iterable<String> departments) {

        //clear previous search parameters in case some of these are null
        client.resetTerms();

        client.setSearchTerms(courseNumber, start, end, keyword, professor, days);

        String offset = Integer.toString(counter);
        try {
            Course.createTable(conn, offset);
            Section.createTable(conn, offset);
        } catch(SQLException e) {
            e.printStackTrace();
            return;
        }

        if(null != departments) {
            for (String dept : departments) {
                try {
                    new Parser(client.getResults(dept)).addToTable(conn, offset);
                } catch (IOException e) {
                }
            }
        }
        else {
            try {
                new Parser(client.getResults()).addToTable(conn, offset);
            } catch (IOException e) { }
        }

        ++counter;
    }


}
