import com.gargoylesoftware.htmlunit.html.HtmlOption;
import cunyfirst.CunyFirstClient;
import cunyfirst.ID;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import parser.CourseData;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Data {
    CunyFirstClient client = new CunyFirstClient();
    Connection conn;

    public Data(Connection c) {
        conn = c;
    }

    public List<String> getSchools() {
        return client.getSelect(ID.selectSchool).getOptions().stream().map(HtmlOption::getText).collect(Collectors.toList());
    }

    //assume setup was called, otherwise returns empty list
    public List<String> getDepts() {
        return client.getSelect(ID.selectDept).getOptions().stream().map(HtmlOption::getValueAttribute).collect(Collectors.toList());
    }

    public List<String> getSemesters() {
        return client.getSelect(ID.selectTerm).getOptions().stream().map(HtmlOption::getText).collect(Collectors.toList());
    }

    public void addCourses(String school) throws SQLException {
        final String table = school.replace(" ","_");
        CourseData.createTable(conn, table);
        PreparedStatement st = conn.prepareStatement("SELECT * FROM "+table+" WHERE dept=? and nbr=?;");
        ResultSet rs;
        client.setSchool(school);
        for(String sem: getSemesters()) {
            client.setup(school, sem);
            for (String dept : getDepts()) {
                if (ID.skippedDepts.contains(dept)) {
                    continue;
                }
                try {
                    Element results = Jsoup.parse(client.getResults(dept).asXml());
                    Elements courses = Selector.select(ID.course, results);
                    for (Element c : courses) {
                        //check for primary key before loading next page
                        String temp[] = Selector.select(ID.courseName, c).get(0).ownText().split(" - ")[0].split(" ");
                        st.setString(1, temp[0].substring(1));
                        st.setString(2, temp[1]);
                        rs = st.executeQuery();

                        //result set is empty
                        if(!rs.isBeforeFirst()) {
                            String key = Selector.select(ID.sectionNbr, c).get(0).id();
                            CourseData cd = new CourseData(client.getSection(key));
                            try {
                                cd.addToTable(conn, table);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
