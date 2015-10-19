package parser;

import cunyfirst.ID;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

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

        Elements secs = Selector.select(ID.section, elem);
        sections = new ArrayList<>(secs.size());
        secs.stream().forEach(s -> sections.add(new Section(s)));
    }

    public final String dept;
    public final String number;
    public final String name;
    public final String components;
    public final ArrayList<Section> sections;
}
