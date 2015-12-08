package search;

import java.sql.*;
import java.util.List;
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
    
    public static Search createSearch(Connection c, int id_num, String college, String term) {
        Search result;
        try {
            result = new CunyFirstSearch(c, id_num);
            result.selectTerm(college.toUpperCase(), term);
        }   catch (RuntimeException e) {
            // use BackupSearch if CunyFirst is down
            // this in NO WAY covers all cases 
            // but it's the only way I've ever found to detect connection issues
            result = new BackupSearch(c, id_num);
            result.selectTerm(college, term);
        }
        return result;
    }
    
    abstract public void selectTerm(String school, String semester);

    abstract public void find(MatchValuePair courseNumber,
                    Integer start, Integer end,
                    String keyword, String professor,
                    int[] days, List<String> departments,
                    boolean[] errors);

}
