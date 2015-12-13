package search.parser;

import search.cunyfirst.ID;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * Parses course-specific information and creates a list of sections
 * @author Kat
 */
public class Course {
    /**
     * @param elem the div containing course info and all section divs
     */
    public Course(Element elem) {
        // split course dept, number, name
        String fullTitle = Selector.select(ID.courseName, elem).get(0).ownText();
        String title = fullTitle.substring(1,fullTitle.length()-1);
        String[] temp = title.split(" - ");
        name = temp[1];
        temp = temp[0].split(" ");
        dept = temp[0];
        number = temp[1];

        // parse sections
        Elements secs = Selector.select(ID.section, elem);
        sections = new ArrayList<>(secs.size());
        secs.stream().forEach(s -> sections.add(new Section(s)));
    }

    /**
     * Add each section to the given table
     * @param conn the database connection
     * @param tablename the name of the table to add the data to
     */
    public void addToTable(Connection conn, String tablename) {
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
