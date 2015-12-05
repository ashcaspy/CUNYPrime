package search;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import search.cunyfirst.CunyFirstClient;
import search.cunyfirst.ID;
import search.cunyfirst.MatchValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import search.parser.CourseData;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;


//retrieves and stores data that will never change
public class InstitutionData {
    CunyFirstClient client = new CunyFirstClient();
    Connection conn;

    public InstitutionData(Connection c) {
        conn = c;
    }

    public List<HtmlOption> getSchools() {
        return client.getSelect(ID.selectSchool).getOptions();
    }

    //assume setup was called, otherwise returns empty list
    public List<String> getDepts() {
        return client.getSelect(ID.selectDept).getOptions().stream().map(HtmlOption::getValueAttribute)
                .filter(d -> !d.isEmpty() && !ID.skippedDepts.contains(d)).collect(Collectors.toList());
    }

    public List<String> getSemesters() {
        return client.getSelect(ID.selectTerm).getOptions().stream().map(HtmlOption::getText).collect(Collectors.toList());
    }

    private final String schoolTable = "schools";

    public void createSelectionTables() throws SQLException {
        Statement sch = conn.createStatement();
        sch.executeUpdate("CREATE TABLE IF NOT EXISTS "+schoolTable+"(" +
                "name varchar(30)," +
                "id varchar(10)," +
                "PRIMARY KEY(id)" +
                ");");
        PreparedStatement st = conn.prepareStatement("insert into "+schoolTable+" values(?,?);");
        for(HtmlOption op: getSchools()) {
            String school = op.getText();
            if(school.isEmpty()) {
                continue;
            }
            String id = op.getValueAttribute();
            st.setString(1, school);
            st.setString(2, id);
            st.executeUpdate();

            client.setSchool(school);

            String depts_table = "depts_"+id;
            sch.executeUpdate("create table if not exists "+depts_table+"("+
                    "college varchar(6)," +
                    "dept varchar(6)," +
                    "primary key(college, dept)" +
                    ");");

            String college_terms = "terms_"+id;
            sch.executeUpdate("create table if not exists "+college_terms+"(" +
                    "college varchar(6)," +
                    "term varchar(20)," +
                    "primary key(college, term)" +
                    ");");
            PreparedStatement insertTerm = conn.prepareStatement("insert into "+college_terms+" values(?,?);");
            PreparedStatement insertDept = conn.prepareStatement("insert into "+depts_table+" values(?,?);");
            for(String term: getSemesters()) {
                if(term.isEmpty()) {
                    continue;
                }
                insertTerm.setString(1, id);
                insertTerm.setString(2, term);
                insertTerm.executeUpdate();

                client.setup(school, term);

                for(String dept: getDepts()) {
                    if(dept.isEmpty()) {
                        continue;
                    }
                    insertDept.setString(1,id);
                    insertDept.setString(2,dept);
                    try {
                        insertDept.executeUpdate();
                    } catch (SQLException e) {}
                }
            }
        }
    }
    
    
    public static String coursesTablename(String school_id) {
        return "college_courses" + school_id;
    }
    
    public void createAllCourseTables() {
        try {
            String id;
            ResultSet ids = conn.createStatement().executeQuery("select id from " + schoolTable + ";");
            while(ids.next()) {
                id = ids.getString(1);
                CourseData.createTable(conn, coursesTablename(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCourses(String school) throws SQLException {
        PreparedStatement st = conn.prepareStatement("select id from "+schoolTable+" where name=?;");
        st.setString(1, school);
        ResultSet r = st.executeQuery();
        r.next();
        String id = r.getString(1);
        final String table = coursesTablename(id);
        st = conn.prepareStatement("SELECT * FROM "+table+" WHERE dept=? and nbr=?;");
        ResultSet rs;
        client.setSchool(school);
        for(String sem: getSemesters()) {
            client.setup(school, sem);
            for (String dept : getDepts()) {
                if (ID.skippedDepts.contains(dept)) {
                    continue;
                }
                try {
                    //get all courses for each department
                    client.setSearchTerms(new MatchValuePair(ID.greaterThan, "0"), null, null, null, null, null);
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
