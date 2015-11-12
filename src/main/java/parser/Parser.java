package parser;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import cunyfirst.ID;

import org.jsoup.Jsoup;
import org.jsoup.select.Selector;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.SQLException;

public class Parser {
    public Parser(HtmlPage page) throws IOException {
        Element doc = Jsoup.parse(page.asXml());
        Elements courseElements = Selector.select(ID.course, doc);

        courses = new ArrayList<>(courseElements.size());

        for(Element c: courseElements) {
            courses.add(new Course(c));
        }
    }
    private ArrayList<Course> courses;

    public void addToTable(Connection conn, String offset) {
        for(Course course: courses) {
            try {
                course.addToTable(conn, offset);
            } catch (SQLException e) {}
        }
    }

}

