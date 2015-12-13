package search.parser;

/**
 * for when Cunyfirst returns an error on an attempted search
 * @author Kat
 */
public class SearchError extends Exception {
    // the message returned
    public final String msg;
    
    public SearchError(String errorMsg) {
        msg = errorMsg;
    }
}
