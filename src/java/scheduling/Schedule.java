package scheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Created by gracie on 11/19/2015.
 */

public class Schedule {

    private List<Day> mySchedule;
    private final String week [];
    
    /**
     * Constructor
     */
    public Schedule (){
        mySchedule = new ArrayList<>();
        final int size = 7;
        for (int i = 0; i < size; i++){
            mySchedule.add(new Day(i));
        }
        week = new String[] {"Su","Mo", "Tu", "We", "Th", "Fr", "Sa"};
    }


    /**
     * A function that takes in a String array containing available times for selected days, and sets those values for those days.
     * @param timesArr, A string array containing available or unavailable time slots for selected days.
     */
     public void setTimes (String timesArr [], boolean isOpenTimes){

        String temp[];
        int prevNum = 0;
        int index = 0;
        final int indexOf2 = 2;
        final int indexOf3 = 3;

        if(timesArr.length > 0){
            Arrays.sort(timesArr);
            for (int i = 0; i < timesArr.length; i++){
                System.out.println(timesArr[i]);
            }
        }

        for (int i = 0; i < timesArr.length; i++){
            temp = timesArr[i].split("-");

            if(temp.length > 0) {

                if(Integer.parseInt(temp[indexOf2].replaceAll(" ", "")) != index) {
                    if(i != 0){
                        if(isOpenTimes){
                            mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).y = prevNum + 1;
         
                        } else {
                            mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() -1 ).y = prevNum + 1;
                        }
                    }
                    index = Integer.parseInt(temp[indexOf2].replaceAll(" ", ""));
                    if(isOpenTimes){
                        mySchedule.get(index).addToOpenTimes(new Pair());
                        mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));
                    } else {
                        mySchedule.get(index).addToCloseTimes(new Pair());
                        mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));


                    }
                    prevNum = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));
                }

                if (Math.abs(Integer.parseInt(temp[indexOf3].replaceAll(" ", "")) - prevNum) > 1) {
                    if(Integer.parseInt(temp[indexOf3]) != prevNum) {
                        if(isOpenTimes){
                            mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).y = prevNum;
                        } else {
                            mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - 1).y = prevNum;

                        }
                    }
                    mySchedule.get(index).addToOpenTimes(new Pair());
                    if(Integer.parseInt(temp[indexOf3]) != prevNum) {
                        if(isOpenTimes){
                            mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));
                        } else {
                            mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));

                        }
                    }
                }
                if(Integer.parseInt(temp[indexOf3])!= prevNum) {

                    prevNum = Integer.parseInt(temp[indexOf3].replaceAll(" ",""));
                    if(isOpenTimes){
                        mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).y = prevNum;
                    } else {
                        mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - 1).y = prevNum;

                    }
                }


            }
        }
    }


    public int getSize(){
        return mySchedule.size();
    }

    public Day getElementFromSchedule(int index){
        if (index > -1 && index < mySchedule.size()){
            return mySchedule.get(index);
        }
        return null;
    }

    public String[] getWeek(){
        return week; 
    }

    /**
     * A function that prints the contents of the mySchedule array of Days.
     */
    public void print(){
        for(int i = 0; i < mySchedule.size(); i++){
            mySchedule.get(i).print();
        }
    }
}
