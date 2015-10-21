package parser;

import com.gargoylesoftware.htmlunit.html.DomElement;
import cunyfirst.ID;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

        components = firstSection.getElementById(ID.courseComponents).getTextContent().trim();
        DomElement required = firstSection.getElementById(ID.prereqs);
        if(null == required) {
            requirements = "";
        }
        else {
            requirements = required.getTextContent().trim();
        }

        Elements secs = Selector.select(ID.section, elem);
        sections = new ArrayList<>(secs.size());
        secs.stream().forEach(s -> sections.add(new Section(s)));
    }

    public void addToTable(Connection conn) throws SQLException {
        PreparedStatement st = conn.prepareStatement("INSERT INTO courses VALUES (?,?,?,?,?)");
        PreparedStatement insertSection;

        st.setString(1, dept);
        st.setString(2, number);
        st.setString(3, name);
        st.setString(4, components.replaceAll(" Required", ""));
        st.setString(5, requirements);

        st.executeUpdate();

        for(Section sec: sections) {
            insertSection = sec.prepareStatement(conn);
            insertSection.setString(1, dept);
            insertSection.setString(2, number);
            insertSection.executeUpdate();
        }
    }

    public final String dept;
    public final String number;
    public final String name;
    public final String components;
    public final String requirements;
    public final ArrayList<Section> sections;
}
