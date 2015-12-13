package search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import search.cunyfirst.ID;
import search.cunyfirst.MatchValuePair;
import search.parser.Section;

/** Search our own, limited and possibly out-of-date database 
 * in the event CunyFirst is down.
 *
 * @author Kat
 */
public class BackupSearch extends Search {
    protected BackupSearch(Connection c, int id) {
        super(c, id);
    }
    
    private String masterTable;
    
    /**
     * set school to search
     * @param school school id
     * @param semester ignored, here for compatibility reasons. Sections are from Spring 2016
     */
    public void selectTerm(String school, String semester) {
        masterTable = Section.tablename + school;
        // ignore semester since the tables don't keep that info
    }
    
    /**
     * insert sections
     * @param courseNumber course number and comparison
     * @param start min start hour
     * @param end max end hour
     * @param keyword unused
     * @param professor uses contains
     * @param days 
     * @param departments departments to search for
     * @param errors boolean array to set if there are any errors
     */
    public void find(MatchValuePair courseNumber,
                    Integer start, Integer end,
                    String keyword, String professor,
                    int[] days, List<String> departments,
                    boolean[] errors) {
        
        PreparedStatement insert;
        
        int before = 0, after = 0; // well if the search fails they would be
        
        try {
            // todo: handle failed inserts gracefully
            Section.createTable(conn, tableName());
            
            before = countResults();
            
            // create select statement
            
            String depts, cnbr, allDays;
            
            // check departments
            if(null == departments) {
                // instead of checking whether to use WHERE or not
                // let this evaluate to true every time
                depts = "";
            } else {
                // quote all departments and arrange them as ('x', 'y', ...)
                depts = " AND cdept IN (" + String.join(",", departments.stream()
                                .map(d -> "'"+d+"'").collect(Collectors.toList())) +
                                ")";
            }
            
            // check courseNumber
            if(null == courseNumber) {
                // consistency is nice
                cnbr = ">'0'";
            } else {
                if(ID.exact.equals(courseNumber.comparison)) {
                    cnbr = "='" + courseNumber.value + "'";
                } else if(ID.contains.equals(courseNumber.comparison)) {
                    // note the space
                cnbr = " LIKE '%" + courseNumber.value + "%'";
                } else if(ID.lessThan.equals(courseNumber.comparison)) {
                    cnbr = "<='" + courseNumber.value + "'";
                } else { // greater than
                    cnbr = ">='" + courseNumber.value + "'";
                }
            }
            
            // check days
            if(days != null && days.length > 0) {
                // todo: probably bugged
                String[] checks = new String[days.length];
                for(int i=0; i<days.length; ++i) {
                    checks[i] = "days LIKE '%"+ID.daysPrefixes[i]+"%'";
                }
                allDays = " AND " + String.join(" AND ", checks) + " ";
            } else {
                allDays = "";
            }
            

            insert = conn.prepareStatement("INSERT INTO " + tableName() + " SELECT * FROM "+ masterTable +
                    " WHERE " + " cnbr" + cnbr + depts + allDays +
                    " AND starttime>=?" +
                    " AND endtime<=?" +
                    " AND instructor LIKE ?;");
            
            //check times
            // search within a half hour of the cutoff
            final int minutes = 30;

            // set remaining search parameters
            if(null != start) {
                int hour = (start-1) * 100;
                insert.setInt(1, hour + minutes);
            } else {
                insert.setInt(1, 0);
            }
            if(null != end) {
                int hour = end * 100;
                insert.setInt(2, hour + minutes);
            } else {
                insert.setInt(2, 2400);
            }
            
            // ignore keyword we can't really use it here
            
            // professor
            if(professor != null) {
                insert.setString(3, "%"+professor+"%");
            } else {
                insert.setString(3, "%");
            }
            
            insert.execute();
            
            after = countResults();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // check if there were no results
        if(before == after) {
            ErrorCode.NORESULTS.setArray(errors);
        }
        else {
            ErrorCode.SUCCESS.setArray(errors);
        }
    }
    
    /**
     * @return counts rows in tableName()
     */
    private int countResults() {
        try {
            PreparedStatement count;
            ResultSet resultCount;
            count = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName());
            resultCount = count.executeQuery();
            resultCount.next();
            return resultCount.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
