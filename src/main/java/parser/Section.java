package parser;

import cunyfirst.ID;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

public class Section {
    public Section(Element elem) {
        time = Selector.select(ID.sectionTime, elem).get(0).ownText();
        room = Selector.select(ID.sectionRoom, elem).get(0).ownText();
        instructor = Selector.select(ID.instructor, elem).get(0).ownText();
        nbr = Selector.select(ID.sectionNbr, elem).get(0).ownText();
    }

    public String toString() {
        return nbr+" "+time+" "+room+" "+instructor;
    }



    public final String nbr;
    public final String time;
    public final String room;
    public final String instructor;
}