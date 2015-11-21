import java.util.ArrayList;
import java.util.List;

/**
 * Created by gracie on 11/19/2015.
 */

public class Schedule {

    private List<Day> mySchedule;

    /**
     * Constructor
     */
    public Schedule (){
        mySchedule = new ArrayList<>();
    }


    /**
     * A construtor that takes in two String arrays, and sets the member variables according to values in the arrays.
     * @param openTimes, A string array containing open time slots for selected days.
     * @param closedTimes, A string array containing unavailable time slots for selected days.
     */
    public Schedule(String [] openTimes, String [] closedTimes){
        mySchedule = new ArrayList<>();
        String temp[];
        int x = -1;
        Day day = new Day();
        mySchedule.add(day);
        int prevY = 0;
        for (int i = 0; i < openTimes.length; i++){
            temp = openTimes[i].split("-");
            if(temp.length > 0) {
                if(mySchedule.get(mySchedule.size() -1).getDay() != Integer.parseInt(temp[2])){
                    if(i != 0){
                        day = new Day();
                        mySchedule.add(day);
                    }
                    mySchedule.get(mySchedule.size() - 1).setDay(Integer.parseInt(temp[2]));

                }
                if(x == -1){
                    mySchedule.get(mySchedule.size() -1).addToOpenTimes(new Pair());
                    if(i == 0){
                        mySchedule.get(mySchedule.size() - 1).getOpenTimeElement(mySchedule.get(mySchedule.size() -1).getOpenTimeSize() - 1).x = Integer.parseInt(temp[3]);
                        x = 0;

                    } else {
                        if (Math.abs(Integer.parseInt(temp[3]) - mySchedule.get(mySchedule.size() -1).getOpenTimeElement(mySchedule.get(mySchedule.size() - 1).getOpenTimeSize() - 2).y) > 1) {
                            mySchedule.get(mySchedule.size() - 1).getOpenTimeElement(mySchedule.get(mySchedule.size() - 1).getOpenTimeSize() - 1).x = Integer.parseInt(temp[3]);
                            x = 0;

                        } else if (Math.abs(Integer.parseInt(temp[3]) - mySchedule.get(mySchedule.size() -1).getOpenTimeElement(mySchedule.get(mySchedule.size() -1).getOpenTimeSize() - 2).y) == 1) {
                            mySchedule.get(mySchedule.size() - 1).getOpenTimeElement(mySchedule.get(mySchedule.size() -1).getOpenTimeSize() - 1).x = prevY;
                            mySchedule.get(mySchedule.size() - 1).getOpenTimeElement(mySchedule.get(mySchedule.size() -1).getOpenTimeSize() - 1).y =  Integer.parseInt(temp[3]);
                            prevY = Integer.parseInt(temp[3]);
                        }
                    }

                } else if(x == 0){
                    mySchedule.get(mySchedule.size() - 1).getOpenTimeElement(mySchedule.get(mySchedule.size() - 1).getOpenTimeSize() - 1).y  = Integer.parseInt(temp[3]);
                    prevY = Integer.parseInt(temp[3]);
                    x = -1;
                }
            }
        }

        /*for (int i = 0; i < mySchedule.size();i++){
            mySchedule.get(i).print();
        }*/


    }
}
