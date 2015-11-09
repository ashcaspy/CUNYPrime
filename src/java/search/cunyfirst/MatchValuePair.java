package search.cunyfirst;
import scala.actors.threadpool.Arrays;

import java.lang.IllegalArgumentException;
import java.util.List;

public class MatchValuePair {
    public MatchValuePair(String comparison, String value) {
        if(!validComps.contains(comparison)) {
            throw new IllegalArgumentException();
        }
        this.comparison = comparison;
        this.value = value;
    }
    public final String comparison;
    public final String value;

    private static final List<String> validComps = Arrays.asList(new String[] {ID.lessThan, ID.greaterThan, ID.exact});
}
