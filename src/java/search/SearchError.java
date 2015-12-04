package search;

/**
 *
 * @author Kat
 */

// thrown when a search fails for whatever reason
// has the error message and the criteria for the failed search
public class SearchError extends Exception {
    public SearchError(String errorMessage, MatchValuePair courseNumber, 
            Integer start, Integer end, String keyword, String professor,
            int[] days, String dept) {
        this.errorMessage = errorMessage;
        this.courseNumber = courseNumber;
        this.start = start;
        this.end = end;
        this.keyword = keyword;
        this.professor = professor;
        this.days = days;
        this.dept = dept;
    }
    public final String errorMessage;
    public final MatchValuePair courseNumber;
    public final Integer start;
    public final Integer end;
    public final String keyword;
    public final String professor;
    public final int[] days;
    
    // only one dept since searches aren't really run 
    // with multiple depts and thus only one failed
    // will probably be null unless the error was "no results"
    public final String dept;    
}
