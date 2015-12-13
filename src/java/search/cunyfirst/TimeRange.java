package search.cunyfirst;

/**
 * A pair of integers representing hours, meant to be used as one time parameter with ID.between comparison
 * @author Kat
 */
public class TimeRange {
    public TimeRange(int min, int max) {
        this.min = min;
        this.max = max;
    }
    public final int min;
    public final int max;
}
