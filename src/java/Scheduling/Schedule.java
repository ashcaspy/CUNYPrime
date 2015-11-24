import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

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
        final int size = 7;
        for (int i = 0; i < size; i++){
            mySchedule.add(new Day(i));
        }
    }


    /**
     * A function that takes in aString arrays containing available times for selecte days, and sets the member variables according to values in the arrays.
     * @param timesArr, A string array containing available or unavailable time slots for selected days.
     */
    public void setOpenTimes (String timesArr []){

        String temp[];
        int prevNum = 0;
        int index = 0;
        final int indexOf2 = 2;
        final int indexOf3 = 3;

        Arrays.sort(timesArr);

        for (int i = 0; i < timesArr.length; i++){
            temp = timesArr[i].split("-");

            if(temp.length > 0) {

                if(Integer.parseInt(temp[indexOf2].replaceAll(" ", "")) != index) {
                    if(i != 0){
                        mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).y = prevNum;
                    }
                    index = Integer.parseInt(temp[indexOf2].replaceAll(" ", ""));
                    mySchedule.get(index).addToOpenTimes(new Pair());
                    mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));
                    prevNum = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));
                }

                if (Math.abs(Integer.parseInt(temp[indexOf3].replaceAll(" ", "")) - prevNum) > 1) {
                    mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).y = prevNum;
                    mySchedule.get(index).addToOpenTimes(new Pair());
                    mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ",""));
                }
                prevNum = Integer.parseInt(temp[indexOf3].replaceAll(" ",""));
                mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).y = prevNum;


            }
        }
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
