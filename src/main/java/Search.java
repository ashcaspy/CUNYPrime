import cunyfirst.CunyFirstClient;
import cunyfirst.MatchValuePair;
import cunyfirst.TimeRange;
import parser.*;

import java.io.IOException;
import java.sql.Connection;

public class Search {
    private CunyFirstClient client = new CunyFirstClient();
    private final Connection conn;

    public Search(Connection c) {
        conn = c;
    }

    public Search(Connection c, String college, String semester) {
        conn = c;
        selectTerm(college, semester);
    }

    public void selectTerm(String school, String semester) {
        client.setup(school, semester);
    }

    public void find(MatchValuePair courseNumber,
                    TimeRange start, TimeRange end,
                    String keyword, String professor,
                    int[] days, Iterable<String> departments) {
        client.setSearchTerms(courseNumber, start, end, keyword, professor, days);

        Course.createTable(conn);
        Section.createTable(conn);

        for(String dept : departments) {
            try {
                new Parser(client, client.getResults(dept)).addToTable(conn);
            } catch (IOException e) { }
        }
    }
}
