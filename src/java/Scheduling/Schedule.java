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
        final int size = 5;
        for (int i = 0; i < size; i++){
            mySchedule.add(new Day());
        }
    }


    /**
     * A construtor that takes in two String arrays, and sets the member variables according to values in the arrays.
     * @param timesArr, A string array containing available or unavailable time slots for selected days.
     * @param open, A boolean indicating whether the timesArr contains available or unavailable time slots for selected days.
     */
    public void SetTimes (String [] timesArr, boolean open){

        String temp[];
        int x = -1;
        int prevY = 0;
        int index = 0;
        int indexOf2 = 2;
        int indexOf3 = 3;


        for (int i = 0; i < timesArr.length; i++){
            temp = timesArr[i].split("-");

            if(temp.length > 0) {

                if(Integer.parseInt(temp[indexOf2].replaceAll(" ", "")) != index) {
                    index = Integer.parseInt(temp[indexOf2].replaceAll(" ", ""));
                    mySchedule.get(index).setDay(index);
                }

                if(x == -1){
                    if(open) {
                        mySchedule.get(index).addToOpenTimes(new Pair());
                    } else {
                        mySchedule.get(index).addToCloseTimes(new Pair());

                    }
                    if(i == 0) {
                        if(open) {
                            mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));
                        } else {
                            mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));

                        }
                        x = 0;

                    } else {
                        if(open){
                            if (Math.abs(Integer.parseInt(temp[indexOf3].replaceAll(" ","")) - mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - indexOf2).y) > 1) {
                                mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));
                                x = 0;

                            } else if (Math.abs(Integer.parseInt(temp[indexOf3].replaceAll(" ","")) - mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - indexOf2).y) == 1) {
                                mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).x = prevY;
                                mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).y =  Integer.parseInt(temp[indexOf3].replaceAll(" ",""));
                                prevY = Integer.parseInt(temp[indexOf3].replaceAll(" ",""));
                            }
                        } else {
                            if (Math.abs(Integer.parseInt(temp[indexOf3].replaceAll(" ","")) - mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - indexOf2).y) > 1) {
                                mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - 1).x = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));
                                x = 0;

                            } else if (Math.abs(Integer.parseInt(temp[indexOf3].replaceAll(" ","")) - mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - indexOf2).y) == 1) {
                                mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - 1).x = prevY;
                                mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - 1).y =  Integer.parseInt(temp[indexOf3].replaceAll(" ",""));
                                prevY = Integer.parseInt(temp[indexOf3].replaceAll(" ",""));
                            }
                        }
                    }

                } else if(x == 0){
                    if(open) {
                        mySchedule.get(index).getOpenTimeElement(mySchedule.get(index).getOpenTimeSize() - 1).y = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));
                    } else {
                        mySchedule.get(index).getClosedTimeElement(mySchedule.get(index).getCloseTimeSize() - 1).y = Integer.parseInt(temp[indexOf3].replaceAll(" ", ""));

                    }

                    prevY = Integer.parseInt(temp[indexOf3].replaceAll(" ",""));
                    x = -1;
                }

            }
        }
    }

    public void print(){
        for(int i = 0; i < mySchedule.size(); i++){
            mySchedule.get(i).print();
        }
    }
}
