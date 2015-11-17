package parser;

import cunyfirst.ID;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;


public class Course {
    public Course(Element elem) {
        String fullTitle = Selector.select(ID.courseName, elem).get(0).ownText();
        String title = fullTitle.substring(1,fullTitle.length()-1);
        String[] temp = title.split(" - ");
        name = temp[1];
        temp = temp[0].split(" ");
        dept = temp[0];
        number = temp[1];

        Elements secs = Selector.select(ID.section, elem);
        sections = new ArrayList<>(secs.size());
        secs.stream().forEach(s -> sections.add(new Section(s)));
    }

    public void addToTable(Connection conn, String offset) throws SQLException {
        PreparedStatement insertSection;
        for(Section sec: sections) {
            try {
                insertSection = sec.prepareStatement(conn, offset);
                insertSection.setString(1, dept);
                insertSection.setString(2, number);
                insertSection.setString(3, name);
                insertSection.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public final String dept;
    public final String number;
    public final String name;

    public final ArrayList<Section> sections;
}
