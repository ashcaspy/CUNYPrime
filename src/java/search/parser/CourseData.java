package parser;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import cunyfirst.ID;

import java.sql.*;

// holds info for courses table (prereqs, description, etc.)
// I'm assuming none of it will change very often
public class CourseData {
    //page - any section page for the course
    public CourseData(HtmlPage page) {
        //"dept(&nbsp;)* nbr - sec&nbsp;&nbsp; name"
        String title = page.getElementById(ID.secCourseName).getTextContent();

        final String space = "[ \u00a0]";
        String[] temp = title.split(" - ");
        name = temp[1].split(space+"{3}")[1];
        temp = temp[0].split(space+"+");
        dept = temp[0];
        number = temp[1];

        components = page.getElementById(ID.courseComponents).getTextContent()
                .trim().replaceAll(" Required", "");

        DomElement required = page.getElementById(ID.prereqs);
        if(null == required) {
            requirements = "";
        }
        else {
            requirements = required.getTextContent().trim();
        }

        DomElement desc = page.getElementById(ID.courseDescription);
        if(null == desc) {
            description = "";
        }
        else {
            description = desc.getTextContent().trim();
        }

        credits = Float.parseFloat(page.getElementById(ID.units).getTextContent().split(" ")[0]);
    }

    public static void createTable(Connection conn, String tablename) throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tablename + "(" +
                        "dept varchar(6)," +
                        "nbr varchar(7)," +
                        "name varchar(90)," +
                        "components varchar(60)," +
                        "requirements varchar(300)," +
                        "description varchar(1300)," +
                        "credits float," +
                        "PRIMARY KEY(dept, nbr)" +
                        ")");
    }

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
