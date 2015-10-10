package parser;

import cunyfirst.ID;

import org.jsoup.Jsoup;
import org.jsoup.select.Selector;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    public Parser(String page) {
        courses = Selector.select(ID.course, Jsoup.parse(page));
        //getNames();
        //getSections(courses.get(0));
        map = new HashMap<>(courses.size());
        for(Element c: courses) {
            String name = getName(c);
            map.put(name, getSections(c));
        }
    }
    private Elements courses;
    private HashMap<String, ArrayList<Section>> map;

    public String getName(Element course) {
        return Selector.select(ID.courseName, course).get(0).ownText().split(" - ")[0].trim()
                .substring(1); //remove whatever that is at the beginning
                //.replaceAll("[^ a-zA-Z0-9"+"\\p{Punct}"+"]", "");
    }

    public ArrayList<Section> getSections(Element course) {
        Elements sections = Selector.select(ID.section, course);
        String time, room, instructor, secNbr;
        ArrayList<Section> list = new ArrayList<>(sections.size());
        for(Element s: sections) {
            time = Selector.select(ID.sectionTime, s).get(0).ownText();
            room = Selector.select(ID.sectionRoom, s).get(0).ownText();
            instructor = Selector.select(ID.instructor, s).get(0).ownText();
            secNbr = Selector.select(ID.sectionNbr, s).get(0).ownText();

            list.add(new Section(secNbr, time, room, instructor));
        }
        return list;
    }

    public ArrayList<Section> get(String key) {
        return map.get(key);
    }

}

