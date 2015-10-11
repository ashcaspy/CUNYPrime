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

}

