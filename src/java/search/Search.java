package search;

import java.sql.*;
import java.util.List;
import search.cunyfirst.MatchValuePair;
import search.parser.Section;

/**
 * Runs searches for sections matching given criteria and adds the results to tableName()
 * @author Kat
 */
public abstract class Search {
    /**
     * 
     * @param c database connection
     * @param id_num id to create a unique table name
     */
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

    /**
     * @return the table name used to store search results
     */
    public String tableName() {
        return Section.tablename + "_" + id;
    }
    
    /**
     * Tries to connect to Cunyfirst, if it fails use BackupSearch
     * @param c database connection
     * @param id_num user id
     * @param college college id to select
     * @param term semester string
     * @return the Search instance to use
     */
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
    
    /**
     * sets the school and semester
     * @param school school id
     * @param semester semester string
     */
    abstract public void selectTerm(String school, String semester);

    /**
     * finds sections matching this criteria, adds them to tableName()
     * times are offset by 30 minutes
     * null parameters are ignored
     * @param courseNumber (comparison, value). Defaults to > 0
     * @param start max start hour, defaults to approx. midnight
     * @param end min end hour
     * @param keyword keyword option, only used by CunyFirstSearch
     * @param professor uses contains, Cunyfirst uses only last name
     * @param days 
     * @param departments
     * @param errors 
     */
    abstract public void find(MatchValuePair courseNumber,
                    Integer start, Integer end,
                    String keyword, String professor,
                    int[] days, List<String> departments,
                    boolean[] errors);

}
