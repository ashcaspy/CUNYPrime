package search.parser;

import search.cunyfirst.ID;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import java.util.HashSet;
import java.util.Arrays;
import java.util.regex.*;

public class Section {
    public Section(Element elem) {
        Pattern p = Pattern.compile(ID.dayTimeRegex);
        Matcher m = p.matcher(Selector.select(ID.sectionTime, elem).get(0).ownText());
        int count;
        for(count = 0; m.find(); ++count){ }


        if(count > 0) {
            days = new String[count];
            start = new String[count];
            end = new String[count];
            m.reset();
            for (int i = 0; m.find(); ++i) {
                String[] temp = parseTime(m.group());
                days[i] = temp[0];
                if (temp[1].equals(ID.TBA)) {
                    start[i] = ID.TBA;
                    end[i] = ID.TBA;
                } else {
                    temp = temp[1].split(" - ");
                    start[i] = Integer.toString(hoursToInt(temp[0]));
                    end[i] = Integer.toString(hoursToInt(temp[1]));
                }
            }
        }
        else {
            //everything is TBA
            days = start = end = new String[] {ID.TBA};
        }

        if(count > 1) {
            HashSet<String> starts = new HashSet<>(Arrays.asList(start)), ends = new HashSet<>(Arrays.asList(end));
            //if this happens, then the only reason the timeslots were listed separately is the rooms
            condense = (starts.size() == 1 && ends.size() == 1);
        }
        else {
            //there is only one
            condense = true;
        }

        room = Selector.select(ID.sectionRoom, elem).get(0).ownText();
        instructor = Selector.select(ID.instructor, elem).get(0).ownText();

        //drop "Regular"
        //I don't know what it does and have yet to see a section that doesn't have it
        Element number = Selector.select(ID.sectionNbr, elem).get(0);
        nbr = number.ownText().split(" ")[0];

        open = isOpen(elem);

        id = number.id();
    }

    public final String delimiter = ",";

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

    private boolean isOpen(Element e) {
        //just check the alt attribute because this is easier
        return Selector.select(ID.openClosed, e).get(0).getElementsByTag("img").get(0)
                .attr("alt").equalsIgnoreCase("open");
    }

    //the caller adds course info
    PreparedStatement prepareStatement(Connection conn) throws SQLException {
        PreparedStatement st = conn.prepareStatement("INSERT INTO sections VALUES (?,?,?,?,?,?,?,?,?)");
        st.setString(3, nbr);

        if(start[0].equals(ID.TBA)) {
            st.setNull(4, Types.VARCHAR);
            st.setNull(5, Types.VARCHAR);
        }
        else if(condense) {
            st.setString(4, start[0]);
            st.setString(5, end[0]);
        }
        else {
            st.setString(4, String.join(delimiter, start));
            st.setString(5, String.join(delimiter, end));
        }

        if(days[0].equals(ID.TBA)) {
            st.setNull(6, Types.VARCHAR);
        }
        else if(condense) {
            st.setString(6, String.join("",days));
        }
        else {
            st.setString(6, String.join(delimiter, days));
        }

        if(room.equals(ID.TBA)) {
            st.setNull(7, Types.VARCHAR);
        }
        else {
            st.setString(7, room);
        }
        st.setString(8, instructor);
        st.setBoolean(9, open);
        return st;
    }

    public final String nbr;

    //handle multiple timeslots
    public final String[] start;
    public final String[] end;
    public final String[] days;
    private final boolean condense;

    public final String room;
    public final String instructor;

    // there are three possible open/closed/waitlist values for each section,
    // this distinguishes between open and not-open
    public final boolean open;

    public final String id;

    private final static String tablename = "sections";

    public static void createTable(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tablename + "(" +
                        "cdept varchar(6)," +
                        "cnbr varchar(7)," +
                        "sec varchar(20)," +
                        "starttime varchar(22)," +
                        "endtime varchar(22)," +
                        "days varchar(21)," +
                        "room varchar(40)," +
                        "instructor varchar(200)," +
                        "open boolean" +
                        ")");
    }
}