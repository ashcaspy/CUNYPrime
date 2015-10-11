package parser;

import cunyfirst.ID;

import org.jsoup.Jsoup;
import org.jsoup.select.Selector;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Parser {
    public Parser(String page) {
        Elements courses = Selector.select(ID.course, Jsoup.parse(page));
        map = new HashMap<>(courses.size());
        for(Element c: courses) {
            String name = getName(c);
            map.put(name, getSections(c));
        }
    }
    private HashMap<String, ArrayList<Section>> map;

    public String getName(Element course) {
        //get the whole title, will need it later
        String temp = Selector.select(ID.courseName, course).get(0).ownText();
        return temp.substring(1, temp.length()-1); //remove whatever that is at the ends
                //.replaceAll("[^ a-zA-Z0-9"+"\\p{Punct}"+"]", "");
    }

    public ArrayList<Section> getSections(Element course) {
        Elements sections = Selector.select(ID.section, course);
        ArrayList<Section> list = new ArrayList<>(sections.size());
        sections.stream().forEach(s -> list.add(new Section(s)));
        return list;
    }

    //for testing
    public ArrayList<Section> get(String key) {
        return map.get(key);
    }

    public void addToTable(Connection conn) throws SQLException {
        String[] temp;
        String dept, number, name;
        PreparedStatement st = conn.prepareStatement("INSERT INTO courses VALUES (?,?,?)");
        PreparedStatement insertSection;
        for(String course: map.keySet()) {
            //XXXX NNNNN - Name of Course
            temp = course.split(" - ");
            name = temp[1];
            st.setString(3, name);

            temp = temp[0].split(" ");
            dept = temp[0];
            number = temp[1];
            st.setString(1, dept);
            st.setString(2, number);

            st.executeUpdate();
//            if(number.length() > 5)
                System.out.println(dept+" "+number);

            for(Section sec: map.get(course)) {
                insertSection = sec.prepareStatement(conn);
                insertSection.setString(1, dept);
                insertSection.setString(2, number);

                insertSection.executeUpdate();
            }
        }
    }

}

