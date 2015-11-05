package search.parser;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import search.cunyfirst.CunyFirstClient;
import search.cunyfirst.ID;

import org.jsoup.Jsoup;
import org.jsoup.select.Selector;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Parser {
    public Parser(CunyFirstClient client, HtmlPage page) throws IOException {
        Element doc = Jsoup.parse(page.asXml());
        Elements courseElements = Selector.select(ID.course, doc);

        courses = new ArrayList<>(courseElements.size());

        for(Element c: courseElements) {
            String key = Selector.select(ID.sectionNbr, c).get(0).id();
            courses.add(new Course(c, client.getSection(key)));
        }
    }
    private ArrayList<Course> courses;

    public void addToTable(Connection conn) throws SQLException {
        for(Course course: courses) {
            course.addToTable(conn);
        }
    }

}

