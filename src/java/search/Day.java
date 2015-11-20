import java.util.ArrayList;
import java.util.List;

/**
 * Created by gracie on 11/19/2015.
 */
public class Day {

    private int day;
    private List<Pair> openTimes ;
    private List<Pair> closeTimes;

    public Day(){
        day = 0;
        openTimes = new ArrayList<>();
        closeTimes = new ArrayList<>();
    }

    public void setDay(int day){
        this.day = day;
    }

    public int getDay(){
        return day;
    }



}
