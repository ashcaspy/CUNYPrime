package search.parser;

import search.cunyfirst.ID;

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

    public void addToTable(Connection conn, String tablename) throws SQLException {
        for(Section sec: sections) {
            try {
                sec.addToTable(conn, tablename, this);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public final String dept;
    public final String number;
    public final String name;

    public final ArrayList<Section> sections;
}
