package parser;

import cunyfirst.ID;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

public class Section {
    public Section(Element elem) {
        String[] temp = parseTime(Selector.select(ID.sectionTime, elem).get(0).ownText());
        days = temp[0];
        time = temp[1];
        room = Selector.select(ID.sectionRoom, elem).get(0).ownText();
        instructor = Selector.select(ID.instructor, elem).get(0).ownText();
        nbr = Selector.select(ID.sectionNbr, elem).get(0).ownText();
    }

    public String toString() {
        return nbr+" "+days+" "+time+" "+room+" "+instructor;
    }

    //return strings representing the days and the hours
    private String[] parseTime(String secTime) {
        if(secTime.equals("TBA")) {
            return new String[] {"TBA", "TBA"};
        }
        else {
            return secTime.split(" ", 2);
        }
    }

    public final String nbr;
    public final String time;
    public final String days;
    public final String room;
    public final String instructor;
}