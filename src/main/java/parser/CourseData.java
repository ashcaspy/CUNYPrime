package parser;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import cunyfirst.ID;

// holds info for courses table (prereqs, description, etc.)
// I'm assuming none of it will change very often
public class CourseData {
    //page - any section page for the course
    public CourseData(HtmlPage page) {
        //"dept nbr - sec&nbsp;&nbsp; name"
        String title = page.getElementById(ID.secCourseName).getTextContent();
        String[] temp = title.split("\u00a0\u00a0 ");
        name = temp[1];
        temp = temp[0].split(" - ")[0].split(" ");
        dept = temp[0];
        number = temp[1];

        components = page.getElementById(ID.courseComponents).getTextContent()
                .trim().replaceAll(" Required", "");

        DomElement required = page.getElementById(ID.prereqs);
        if(null == required) {
            requirements = null;
        }
        else {
            requirements = required.getTextContent().trim();
        }

        DomElement desc = page.getElementById(ID.courseDescription);
        if(null == desc) {
            description = null;
        }
        else {
            description = desc.getTextContent().trim();
        }

        credits = Float.parseFloat(page.getElementById(ID.units).getTextContent().split(" ")[0]);
    }

    public final String dept;
    public final String number;
    public final String name;
    public final String components;
    public final String requirements;
    public final String description;
    public final float credits;
}
