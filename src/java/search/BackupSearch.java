package search;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    public BackupSearch(Connection c, Connection src, int id) {
        super(c, id);
        source = src;
    }
    
    private final Connection source;
    
    private String masterTable;
    
    // ignore semester since the tables don't keep that info
    public void selectTerm(String school, String semester) {
        masterTable = Section.tablename + school;
    }
    
    public ErrorCode find(MatchValuePair courseNumber,
                    Integer start, Integer end,
                    String keyword, String professor,
                    int[] days, List<String> departments) {
        
        PreparedStatement insert;
        try {
            Section.createTable(conn, offset());
            String depts, cnbr;
            
            // check departments
            if(null == departments) {
                // instead of checking whether to use WHERE or not
                // let this evaluate to true every time
                depts = "";
            } else {
                // quote all departments and arrange them as ('x', 'y', ...)
                depts = "AND cdept IN (" + String.join(",", departments.stream()
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
            
            
            insert = conn.prepareStatement("INSERT INTO " + tableName() + " SELECT * FROM "+ masterTable +
                    " WHERE " + " cnbr" + cnbr + depts + 
                    " AND starttime>=?" + 
                    " AND endtime<=?;");
            
            // defaults
            insert.setInt(1, 0);
            insert.setInt(2, 2400);
            
            insert.execute();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // this was cunyfirst-specific so let's just assume
        return ErrorCode.SUCCESS;
    }
}
