package search.parser;

import com.gargoylesoftware.htmlunit.html.DomElement;
import search.cunyfirst.ID;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import java.sql.Statement;
import java.util.ArrayList;


public class Course {
    public Course(Element elem, HtmlPage firstSection) {
        String fullTitle = Selector.select(ID.courseName, elem).get(0).ownText();
        String title = fullTitle.substring(1,fullTitle.length()-1);
        String[] temp = title.split(" - ");
        name = temp[1];
        temp = temp[0].split(" ");
        dept = temp[0];
        number = temp[1];

        components = firstSection.getElementById(ID.courseComponents).getTextContent()
                .trim().replaceAll(" Required", "");
        DomElement required = firstSection.getElementById(ID.prereqs);
        if(null == required) {
            requirements = null;
        }
        else {
            requirements = required.getTextContent().trim();
        }

        DomElement desc = firstSection.getElementById(ID.courseDescription);
        if(null == desc) {
            description = "";
        }
        else {
            description = desc.getTextContent().trim();
        }

        credits = Float.parseFloat(firstSection.getElementById(ID.units).getTextContent().split(" ")[0]);

        Elements secs = Selector.select(ID.section, elem);
        sections = new ArrayList<>(secs.size());
        secs.stream().forEach(s -> sections.add(new Section(s)));
    }

    public void addToTable(Connection conn, String offset) throws SQLException {
        PreparedStatement st = conn.prepareStatement("INSERT INTO courses" + offset + " VALUES (?,?,?,?,?,?,?)");
        PreparedStatement insertSection;

        st.setString(1, dept);
        st.setString(2, number);
        st.setString(3, name);
        if(components.split(",").length > 1) {
            st.setString(4, components);
        } else {
            st.setNull(4, Types.VARCHAR);
        }
        if(null != requirements) {
            st.setString(5, requirements);
        } else {
            st.setNull(5, Types.VARCHAR);
        }
        st.setString(5, requirements);
        st.setString(6, description);
        st.setFloat(7, credits);

        st.executeUpdate();

        for(Section sec: sections) {
            try {
                insertSection = sec.prepareStatement(conn, offset);
                insertSection.setString(1, dept);
                insertSection.setString(2, number);
                insertSection.executeUpdate();
            } catch (SQLException e) {}
        }
    }

    public final String dept;
    public final String number;
    public final String name;
    public final String components;
    public final String requirements;
    public final String description;
    public final float credits;
    public final ArrayList<Section> sections;

    private static final String tablename = "courses";

    public static void createTable(Connection conn, String offset) throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tablename + offset + "(" +
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
}
