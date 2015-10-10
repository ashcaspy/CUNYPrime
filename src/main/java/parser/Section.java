package parser;

public class Section {
    public Section(String secNbr, String secTime, String classroom, String instr) {
        nbr = secNbr;
        time = secTime;
        room = classroom;
        instructor = instr;
    }

    public String toString() {
        return nbr+" "+time+" "+room+" "+instructor;
    }

    public final String nbr;
    public final String time;
    public final String room;
    public final String instructor;
}