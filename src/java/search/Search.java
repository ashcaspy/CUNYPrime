package search;

import java.sql.*;
import search.cunyfirst.MatchValuePair;
import search.parser.Section;

/**
 *
 * @author Kat
 */
public abstract class Search {
    public Search(Connection c, int id_num) {
        conn = c;
        id = Integer.toString(id_num);
    }
    
    // the connection used to store results
    protected final Connection conn;
    
    // make tablenames unique
    protected final String id;
    
    //create separate tables for each search run
    protected int counter = 1;

    public String tableName() {
        return Section.tablename + offset();
    }

    protected String offset() {
        return Integer.toString(counter) + "_" + id;
    }

    abstract public void selectTerm(String school, String semester);

    abstract public ErrorCode find(MatchValuePair courseNumber,
                    Integer start, Integer end,
                    String keyword, String professor,
                    int[] days, Iterable<String> departments);

}
