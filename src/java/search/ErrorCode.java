package search;

/**
 *
 * @author Kat
 */
public enum ErrorCode {
    // todo: give error message verbatim instead?
    
    SUCCESS(0),

    NORESULTS(1),
    // "The search returns no results that match the criteria specified."

    MORECRITERIA(-1),
    // "Specify additional selection criteria to narrow your search.
    // needs >= 2
    // should never happen now that, by default,
    // search sets course number and career

    TOOMANY(2),
    // "Your search will exceed the maximum limit of 350 sections.  Specify additional criteria to continue."

    UNKNOWN(-1);
    // some fourth error we don't know about or didn't anticipate
    // I know there's one for not selecting Term/Institution but that should never come up
    
    ErrorCode(int i) {
        index = i;
    }
    
    public final int index;
    public void setArray(boolean[] arr) {
        if(index >= 0) {
            arr[index] = true;
        }
    }
    
    // chooses a value that best represents the error message
    public static ErrorCode fromMsg(String msg) {
        if(msg.contains("maximum")) {
            return TOOMANY;
        } else if(msg.contains("no results")) {
            return NORESULTS;
        } else if (msg.contains("narrow")) {
            return MORECRITERIA;
        } else {
            return UNKNOWN;
        }
    }
    
    // returns whichever of the two given values should take precedent
    public static ErrorCode max(ErrorCode ec1, ErrorCode ec2) {
        
        // check for equivalence
        if(ec1 == ec2) {
            return ec1;
        } 
        // TOOMANY has precedence
        else if(ec1 == TOOMANY || ec2 == TOOMANY) {
            return TOOMANY;
        } 
        
        // NORESULTS does not have precedence, ever
        // since max() would only be used with multifinds 
        // any other result is more improtant
        else if(ec1 == NORESULTS) {
            return ec2;
        } else if(ec2 == NORESULTS) {
            return ec1;
        }
        else {
            // ec1 is in (SUCCESS, MORECRITERIA, UNKNOWN)
            // SUCCESS only supercedes NORESULTS (which is accounted for)
            // and I'm not expecting MORECRITERIA or UNKNOWN and wouldn't know what to do with them
            return ec2;
        }
    }
}
