package search.parser;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import search.cunyfirst.ID;

import java.sql.*;

/**
 * holds static info for courses table (prereqs, description, etc.)
 * @author Kat
 */
public class CourseData {
    /**
     * parses course info from the given page
     * @param page any section page for the course, will only parse course-wide info
     */
    public CourseData(HtmlPage page) {
        //"dept(&nbsp;)* nbr - sec&nbsp;&nbsp; name"
        String title = page.getElementById(ID.secCourseName).getTextContent();

        final String space = "[ \u00a0]";
        String[] temp = title.split(" - ");
        name = temp[1].split(space+"{3}")[1];
        temp = temp[0].split(space+"+");
        dept = temp[0];
        number = temp[1];

        // strip redundant "Required"s
        components = page.getElementById(ID.courseComponents).getTextContent()
                .trim().replaceAll(" Required", "");

        DomElement required = page.getElementById(ID.prereqs);
        // no requirements
        if(null == required) {
            requirements = "";
        }
        else {
            requirements = required.getTextContent().trim();
        }

        DomElement desc = page.getElementById(ID.courseDescription);
        // no description
        if(null == desc) {
            description = "";
        }
        else {
            description = desc.getTextContent().trim();
        }

        // turns out there really are courses worth 3.5
        credits = Float.parseFloat(page.getElementById(ID.units).getTextContent().split(" ")[0]);
    }

    /**
     * Creates a table for course data.
     * Primary key does not work for courses from PERM or STABD
     * @param conn database connection
     * @param tablename the name to use
     * @throws SQLException 
     */
    public static void createTable(Connection conn, String tablename) throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tablename + "(" +
                        "dept varchar(6)," +
                        "nbr varchar(7)," +
                        "name varchar(200)," +
                        "components varchar(60)," +
                        "requirements varchar(500)," +
                        "description varchar(2000)," +
                        "credits float," +
                        "PRIMARY KEY(dept, nbr)" +
                        ")");
    }

    /**
     * insert this course into the table
     * @param conn database connection
     * @param tablename the table to use
     * @throws SQLException 
     */
    public void addToTable(Connection conn, String tablename) throws SQLException {
        PreparedStatement st = conn.prepareStatement("INSERT INTO " + tablename + " VALUES (?,?,?,?,?,?,?)");

        st.setString(1, dept);
        st.setString(2, number);
        st.setString(3, name);
        st.setString(4, components);
        st.setString(5, requirements);
        st.setString(5, requirements);
        st.setString(6, description);
        st.setFloat(7, credits);

        st.executeUpdate();

    }


    public final String dept;
    public final String number;
    public final String name;
    public final String components;
    public final String requirements;
    public final String description;
    public final float credits;
}
