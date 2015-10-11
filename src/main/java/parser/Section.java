package parser;

import cunyfirst.ID;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

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

    //the caller adds course info
    PreparedStatement prepareStatement(Connection conn) throws SQLException {
        PreparedStatement st = conn.prepareStatement("INSERT INTO sections VALUES (?,?,?,?,?,?,?,?)");
        st.setString(3, nbr);

        if(start < 0) {
            st.setNull(4, Types.INTEGER);
            st.setNull(5, Types.INTEGER);
        }
        else {
            st.setInt(4, start);
            st.setInt(5, end);
        }

        if(days.equals(ID.TBA)) {
            st.setNull(6, Types.VARCHAR);
        }
        else {
            st.setString(6, days);
        }

        if(room.equals(ID.TBA)) {
            st.setNull(7, Types.VARCHAR);
        }
        else {
            st.setString(7, room);
        }
        st.setString(8, instructor);
        return st;
    }

    public final String nbr;
    public final int start;
    public final int end;
    public final String days;
    public final String room;
    public final String instructor;
}