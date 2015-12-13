package search.cunyfirst;
import java.util.Arrays;

import java.util.List;

/**
 * Pair with the comparison type to use and the value to check for
 * @author Kat
 */
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

    private static final List<String> validComps = Arrays.asList(
            new String[] {ID.lessThan, ID.greaterThan, ID.exact, ID.contains});
}
