package search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
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
            String depts;
            if(null == departments) {
                // instead of checking whether to use WHERE or not
                // let this evaluate to true every time
                depts = "cdept LIKE '%'";
            } else {
                // quote all departments and arrange them as ('x', 'y', ...)
                depts = "cdept IN (" + String.join(",", departments.stream()
                                .map(d -> "'"+d+"'").collect(Collectors.toList())) +
                                ")";
            }

            insert = conn.prepareStatement("INSERT INTO " + tableName() + " SELECT * FROM "+ masterTable +
                    " WHERE " + depts + ";");
            insert.execute();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // this was cunyfirst-specific so let's just assume
        return ErrorCode.SUCCESS;
    }
}
