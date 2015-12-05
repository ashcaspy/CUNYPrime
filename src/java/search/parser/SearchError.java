package search.parser;

/**
 *
 * @author Kat
 */
public class SearchError extends Exception {
    public final String msg;
    public SearchError(String errorMsg) {
        msg = errorMsg;
    }
}
