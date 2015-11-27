package scheduling;
/**
 * Created by gracie on 11/19/2015.
 */
public class Pair {
    int x;
    int y;

    /**
     * Constructor setting default values.
     */
    Pair(){
        x = -2;
        y= -2;
    }

    /**
     * A Constructor that takes in two integer values and stores them as a pair.
     * @param x, An integer value representing one value of the pair.
     * @param y, An integer value representing the other value of the pair.
     */
    Pair(int x, int y){
        this.x = x;
        this.y = y;
    }


}
