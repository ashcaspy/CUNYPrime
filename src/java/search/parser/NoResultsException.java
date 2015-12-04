package search.parser;

/**
 *
 * @author Kat
 */
public class NoResultsException extends Exception {
    public NoResultsException(String msg) {
        this.msg = msg;
    }
    public final String msg;
}
