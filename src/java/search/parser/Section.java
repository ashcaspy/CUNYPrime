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
            start = new Integer[count];
            end = new Integer[count];
            m.reset();
            for (int i = 0; m.find(); ++i) {
                String[] temp = parseTime(m.group());
                days[i] = temp[0];
                if (temp[1].equals(ID.TBA)) {
                    start[i] = -1;
                    end[i] = -1;
                } else {
                    temp = temp[1].split(" - ");
                    start[i] = hoursToInt(temp[0]);
                    end[i] = hoursToInt(temp[1]);
                }
            }
        }
        else {
            //everything is TBA
            days = new String[] {ID.TBA};
            start = end = new Integer[] {-1};
        }

        if(count > 1) {
            HashSet<Integer> starts = new HashSet<>(Arrays.asList(start)), ends = new HashSet<>(Arrays.asList(end));
            //if this happens, then the only reason the timeslots were listed separately is the rooms
            condense = (starts.size() == 1 && ends.size() == 1);
        }
        else {
            //there is only one
            condense = true;
        }

        // if there are multiple meeting times/places then these fields will
        // be a bit of a mess but I'm not sure how to figure out which space
        // is the divider so for now we'll let them deal with it
        room = Selector.select(ID.sectionRoom, elem).get(0).ownText();
        instructor = Selector.select(ID.instructor, elem).get(0).ownText();

        //drop "Regular"
        //I don't know what it does and have yet to see a section that doesn't have it
        Element number = Selector.select(ID.sectionNbr, elem).get(0);
        nbr = number.ownText().split(" ")[0];

        open = isOpen(elem);
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

    private boolean isOpen(Element e) {
        //just check the alt attribute because this is easier
        return Selector.select(ID.openClosed, e).get(0).getElementsByTag("img").get(0)
                .attr("alt").equalsIgnoreCase("open");
    }

    /**
     * adds this section to sections[offset], may insert multiple rows
     * @param conn - database connection
     * @param offset - the string to choose a unique table of sections
     * @param owner - the Course that owns this section, provides course info
     *
     * In the rare event (according to test data) that this section
     * meets at different times it will insert one row for each timeslot
     */
    void addToTable(Connection conn, String table, Course owner) throws SQLException {
        PreparedStatement st = conn.prepareStatement("INSERT INTO " + table +
                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

        //default count is 1
        st.setInt(11, 1);

        //initial points is 0
        st.setInt(12, 0);

        st.setString(1, owner.dept);
        st.setString(2, owner.number);
        st.setString(3, owner.name);
        st.setString(4, nbr);

        if(room.equals(ID.TBA)) {
            st.setNull(8, Types.VARCHAR);
        }
        else {
            st.setString(8, room);
        }
        st.setString(9, instructor);
        st.setBoolean(10, open);

        if(days[0].equals(ID.TBA)) {
            /* I don't think there are any cases where times were TBA
               and days were not; the other way around seems even less likely
            */
            st.setNull(7, Types.VARCHAR);
            st.setNull(5, Types.INTEGER);
            st.setNull(6, Types.INTEGER);
            st.executeUpdate();
        }
        else if(condense) {
            // the section meets at the same time
            // on different days in different classrooms
            st.setInt(5, start[0]);
            st.setInt(6, end[0]);
            st.setString(7, String.join("", days));
            st.executeUpdate();
        }
        else {
            // insert one row for each timeslot
            for(int i=0; i<days.length; ++i) {
                st.setInt(5, start[i]);
                st.setInt(6, end[i]);
                st.setString(7, days[i]);
                st.setInt(11, i+1);
                st.executeUpdate();
            }
        }
    }

    public final String nbr;

    /* handle multiple timeslots
       eg. a class that meets MoTh 9:45-11AM, Th 10:10-11AM
       the index of each array should match up
       if condense is true, the timeslots are identical
       (except for classroom) and can be stored as a single row
    */
    public final Integer[] start;
    public final Integer[] end;
    public final String[] days;
    private final boolean condense;

    public final String room;
    public final String instructor;

    // there are three possible open/closed/waitlist values for each section,
    // this distinguishes between open and not-open
    public final boolean open;

    public final static String tablename = "_sections";

    public static void createTable(Connection conn, String table) throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + "(" +
                "cdept varchar(6)," +
                "cnbr varchar(7)," +
                "cname varchar(200)," +
                "sec varchar(20)," +
                "starttime int," +
                "endtime int," +
                "days varchar(21)," +
                "room varchar(60)," +
                "instructor varchar(400)," +
                "open boolean," +
                "count int," +
                "points int," +
                "PRIMARY KEY(cdept, cnbr, sec, count)" +
                ")");
    }
}