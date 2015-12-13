package search.parser;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import search.cunyfirst.ID;

import org.jsoup.Jsoup;
import org.jsoup.select.Selector;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Parse courses, sections out of a page of search results
 * @author Kat
 */
public class Parser {
    /**
     * 
     * @param page the results page to parse
     * @throws IOException
     * @throws SearchError when Cunyfirst can't return results
     */
    public Parser(HtmlPage page) throws IOException, SearchError {
        Element doc = Jsoup.parse(page.asXml());
        
        // check for error
        Element error = doc.getElementById(ID.error);
        if(null != error) {
            throw new SearchError(error.ownText());
        }
        
        // find course divs
        Elements courseElements = Selector.select(ID.course, doc);

        courses = new ArrayList<>(courseElements.size());

        // parse courses
        for(Element c: courseElements) {
            courses.add(new Course(c));
        }
    }
    private ArrayList<Course> courses;

    /**
     * insert parsed information into table
     * @param conn database connection
     * @param table table name to use
     */
    public void addToTable(Connection conn, String table) {
        for(Course course: courses) {
            course.addToTable(conn, table);
        }
    }

}