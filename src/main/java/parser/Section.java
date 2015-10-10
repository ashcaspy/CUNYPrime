package parser;

import cunyfirst.ID;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

public class Section {
    public Section(Element elem) {
        String[] temp = parseTime(Selector.select(ID.sectionTime, elem).get(0).ownText());
        days = temp[0];
        if(temp[1].equals(ID.TBA)) {
            start = -1;
            end = -1;
        }
        else {
            temp = temp[1].split(" - ");
            start = hoursToInt(temp[0]);
            end = hoursToInt(temp[1]);
        }
        room = Selector.select(ID.sectionRoom, elem).get(0).ownText();
        instructor = Selector.select(ID.instructor, elem).get(0).ownText();

        //drop "Regular"
        //I don't know what it does and have yet to see a section that doesn't have it
        nbr = Selector.select(ID.sectionNbr, elem).get(0).ownText().split(" ")[0];
    }

    public String toString() {
        return nbr+" "+days+" "+start+" "+end+" "+room+" "+instructor;
    }

    //return strings representing the days and the hours
    private String[] parseTime(String secTime) {
        if(secTime.equals(ID.TBA)) {
            return new String[] {ID.TBA, ID.TBA};
        }
        else {
            return secTime.split(" ", 2);
        }
    }

    private int hoursToInt(String time) {
        int numbers = Integer.parseInt(time.replaceAll("[^\\p{Digit}]", ""));
        //we don't need to care about midnight
        if(time.substring(time.length()-2).equals("PM") && !time.substring(0,2).equals("12")) {
            return numbers + 1200;
        }
        else {
            return numbers;
        }
    }

    public final String nbr;
    public final int start;
    public final int end;
    public final String days;
    public final String room;
    public final String instructor;
}