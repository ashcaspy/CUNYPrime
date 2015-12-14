package search;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import search.cunyfirst.CunyFirstClient;
import search.cunyfirst.ID;
import search.cunyfirst.MatchValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import search.parser.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;


/**
 * retrieves and stores static school information
 * @author Kat
 */
public class InstitutionData {
    CunyFirstClient client = new CunyFirstClient();
    Connection conn;

    /**
     * @param c database connection to use
     */
    public InstitutionData(Connection c) {
        conn = c;
    }

    /**
     * @return the list of school options
     */
    public List<HtmlOption> getSchools() {
        return client.getSelect(ID.selectSchool).getOptions();
    }

    /**
     * Get departments for current school, filter out blanks and skippedDepts
     * @return a list of departments for the current school and semester
     */
    public List<String> getDepts() {
        return client.getSelect(ID.selectDept).getOptions().stream().map(HtmlOption::getValueAttribute)
                .filter(d -> !d.isEmpty() && !ID.skippedDepts.contains(d)).collect(Collectors.toList());
    }

    /**
     * @return semesters (by text) available for current school
     */
    public List<String> getSemesters() {
        return client.getSelect(ID.selectTerm).getOptions().stream().map(HtmlOption::getText).collect(Collectors.toList());
    }

    private final String schoolTable = "schools";

    /**
     * Fill all tables for school ids, semesters, and departments
     * @throws SQLException 
     */
    public void createSelectionTables() throws SQLException {
        Statement sch = conn.createStatement();
        sch.executeUpdate("CREATE TABLE IF NOT EXISTS "+schoolTable+"(" +
                "name varchar(30)," +
                "id varchar(10)," +
                "PRIMARY KEY(id)" +
                ");");
        // insert school ids
        PreparedStatement st = conn.prepareStatement("insert into "+schoolTable+" values(?,?);");
        for(HtmlOption op: getSchools()) {
            // get school name
            String school = op.getText();
            // skip blank
            if(school.isEmpty()) {
                continue;
            }
            // get school id
            String id = op.getValueAttribute();
            st.setString(1, school);
            st.setString(2, id);
            st.executeUpdate();

            // get all other info for this school
            client.setSchool(school);

            // create depts and terms tables
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
            
            // iterate over semesters to get all depts
            for(String term: getSemesters()) {
                // skip blank
                if(term.isEmpty()) {
                    continue;
                }
                // insert semester
                insertTerm.setString(1, id);
                insertTerm.setString(2, term);
                insertTerm.executeUpdate();

                // set semester so depts can be accessed
                client.setup(school, term);

                // insert depts
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
    
    /**
     * 
     * @param school_id school id
     * @return name of the table containing course info for this school
     */
    public static String coursesTablename(String school_id) {
        return "college_courses" + school_id;
    }
    
    /**
     * create course tables for every school, without filling any
     */
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

    /**
     * Add all courses from this school to table
     * @param school_id the school name to use
     * @throws SQLException 
     */
    public void addCourses(String school_id) throws SQLException {
        // get courses table name
        final String table = coursesTablename(school_id);
        
        // use to check if this course is in the table before loading its page
        PreparedStatement st;
        st = conn.prepareStatement("SELECT * FROM "+table+" WHERE dept=? and nbr=?;");
        ResultSet rs;
        
        // iterate over all semesters
        client.setSchool(school_id);
        for(String sem: getSemesters()) {
            client.setup(school_id, sem);
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
                            // load and parse page
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
    
    
    /**
     * create master table for every school without populating it
     */
    public void createSectionTables() {
        try {
            PreparedStatement st = conn.prepareStatement("select id from "+schoolTable);
            ResultSet rs = st.executeQuery();
            String id;
            while(rs.next()) {
                id = rs.getString(1);
                Section.createTable(conn, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * add all sections found in the given departments to a master table
     * @param school_id the school to check
     * @param semester the semester to use
     * @param depts the departments to add
     */
    public void addSections(String school_id, String semester, Iterable<String> depts) {
        client.setup(school_id, semester);
        
        // two criteria - courseNumber and dept
        client.setSearchTerms(new MatchValuePair(ID.greaterThan, "0"), null, null, null, null, null);

        // get depts
        for(String dept: depts) {
            try {
                new Parser(client.getResults(dept)).addToTable(conn, school_id);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SearchError e) {
                System.out.println(dept + ": " + e.msg);
            }
        }
    }

}
