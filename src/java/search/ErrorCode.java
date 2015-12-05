package search;

/**
 *
 * @author Kat
 */
public enum ErrorCode {
    // todo: give error message verbatim instead?
    
    SUCCESS,

    NORESULTS,
    // "The search returns no results that match the criteria specified."

    MORECRITERIA,
    // "Specify additional selection criteria to narrow your search.
    // needs >= 2

    TOOMANY,
    // "Your search will exceed the maximum limit of 350 sections.  Specify additional criteria to continue."

    UNKNOWN;
    // some fourth error we don't know about or didn't anticipate
    // I know there's one for not selecting Term/Institution but that should never come up
    
    // chooses a value that best represents the error message
    public static ErrorCode fromMsg(String msg) {
        if(msg.contains("maximum")) {
            return MORECRITERIA;
        } else if(msg.contains("no results")) {
            return NORESULTS;
        } else if (msg.contains("narrow")) {
            return MORECRITERIA;
        } else {
            return UNKNOWN;
        }
    }
}
