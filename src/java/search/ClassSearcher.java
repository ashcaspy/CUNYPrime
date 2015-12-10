package search;


import connections.MainConfig;
import java.net.URISyntaxException;

import java.sql.*;

public class ClassSearcher {
        public static Connection classSearch() throws URISyntaxException, ClassNotFoundException {
            

            Connection conn;

            try {
                conn = MainConfig.getConnection(); // <--- *******
                System.out.println("GOOD");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("BAD");
                return null;
            }
            /*
            Search searcher = new Search(conn);
            searcher.selectTerm("Hunter College", ID.semester("Fall", 2015));
            searcher.find(
                    new MatchValuePair(ID.greaterThan, "0"), new TimeRange(10, 12), new TimeRange(11, 14), null, null,
                    new int[] {3},
                    Arrays.asList(new String[]{"CSCI", "ANTHC"}));
            searcher.find(
                    null, new TimeRange(12, 13), new TimeRange(13,15), null, null, new int[]{1},
                    Arrays.asList(new String[]{"CSCI", "ENGL", "CHIN"})
            );
            */
            /*
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            */
            return conn;
        }
}
