import com.gargoylesoftware.htmlunit.html.HtmlOption;
import cunyfirst.CunyFirstClient;
import cunyfirst.ID;
import cunyfirst.MatchValuePair;
import cunyfirst.TimeRange;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import parser.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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

    public void addCourses() throws SQLException {
        CourseData.createTable(conn);
        for(String dept: getDepts()) {
            if(ID.skippedDepts.contains(dept)) {
                continue;
            }
            try {
                Element results = Jsoup.parse(client.getResults(dept).asXml());
                Elements courses = Selector.select(ID.course, results);
                for(Element c : courses) {
                    String key = Selector.select(ID.sectionNbr, c).get(0).id();
                    CourseData cd = new CourseData(client.getSection(key));
                    cd.addToTable(conn);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
