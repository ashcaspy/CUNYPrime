package scheduling;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gracie on 11/19/2015.
 */

public class Day {

    private final int day;
    private List<Pair> openTimes ;
    private List<Integer> closeTimes;

    /**
     *  A constructor setting default vaules without parameters.
     */
    public Day(int day){
        this.day = day;
        openTimes = new ArrayList<>();
        closeTimes = new ArrayList<>();

    }


    /**
     * A function that returns the day.
     * @return day, An integer that represents the day of the week (eg. 1 for Monday).
     */
    public int getDay(){
        return day;
    }



    /**
     * A functions that adds an element to the list of open times.
     * @param pair, An instance of Pair that contains a range of open time slot.
     */
    public void addToOpenTimes(Pair pair){
        openTimes.add(pair);
    }


    /**
     * A functions that adds an element to the list of close times.
     * @param time, An integer that represents an unavailable time.
     */
    public void addToCloseTimes(int time){
        closeTimes.add(time);
    }


    /**
     * A function that returns the element in a given index for the openTimes list.
     * @param index, The index in the list desired.
     * @return, A Pair containing an open time slot range.
     */
    public Pair getOpenTimeElement(int index){
        if(index < openTimes.size() && index > -1) {
            return openTimes.get(index);
        }
        return null;
    }


    /**
     * A function that returns the size of the openTimes list.
     * @return, An integer that is the length of the openTimes list.
     */
    public int getOpenTimeSize(){
        return openTimes.size();
    }


    /**
     * A function that returns the element in a given index for the closeTimes list.
     * @param index, The index in the list desired.
     * @return, An integer containing a close time slot range.
     */
    public int getClosedTimeElement(int index){
        if(index < closeTimes.size() && index > -1) {
            return closeTimes.get(index);
        }
        return -1;
    }


    /**
     * A function that returns the size of the closeTimes list.
     * @return, An integer that is the length of the closeTimes list.
     */
    public int getCloseTimeSize() { return closeTimes.size(); }



    /**
     * A function that prints out the available and unavailable time slots for selected days.
     */
    public void print() {
        if (day != -1){
            System.out.println("Day: " + day);
            if (!openTimes.isEmpty()) {
                System.out.println("Available time slots: ");
                for (int i = 0; i < openTimes.size(); i++) {
                    System.out.println(openTimes.get(i).x + "  " + openTimes.get(i).y);
                }
            }
            if (!closeTimes.isEmpty()) {
                System.out.println("Unavailable time slots: ");
                for (int i = 0; i < closeTimes.size(); i++) {
                    System.out.println(closeTimes.get(i));
                }
            }
            System.out.println("-------------------");
        }

    }

}
