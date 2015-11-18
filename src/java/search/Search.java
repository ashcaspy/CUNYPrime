package search;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import search.cunyfirst.CunyFirstClient;
import search.cunyfirst.ID;
import search.cunyfirst.MatchValuePair;
import search.cunyfirst.TimeRange;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import search.parser.Course;
import search.parser.CourseData;
import search.parser.Parser;
import search.parser.Section;


public class Search {
    private CunyFirstClient client = new CunyFirstClient();
    private final Connection conn;
    private final String id;


    //create separate tables for each search run
    private int counter = 1;

    public Search(Connection c, int id) {
        conn = c;
        this.id = Integer.toString(id);
    }

    public Search(Connection c, int id, String college, String semester) {
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
                    TimeRange start, TimeRange end,
                    String keyword, String professor,
                    int[] days, Iterable<String> departments) {

        //clear previous search parameters in case some of these are null
        client.resetTerms();

        client.setSearchTerms(courseNumber, start, end, keyword, professor, days);

        String offset = Integer.toString(counter) + "_" + id;
        try {
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
        if(counter > 2) {
            counter = 1;
        }
    }

}
