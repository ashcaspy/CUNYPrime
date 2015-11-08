
import java.sql.DriverManager;
import java.util.Arrays;

import cunyfirst.*;

import java.sql.*;

class Main {
        public static void main(String[] args) {
            if(args.length != 2) {
                return;
            }

            Connection conn;
            String serverName = "localhost:3306";
            String mydatabase = "cunyfirst";
            String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            try {
                conn = DriverManager.getConnection(url, args[0], args[1]);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
            Search searcher = new Search(conn);
            searcher.selectTerm("Hunter College", ID.semester("Fall", 2015));
            searcher.find(
                    new MatchValuePair(ID.greaterThan, "0"), new TimeRange(10, 12), new TimeRange(11, 14), null, null,
                    new int[] {3},
                    Arrays.asList(new String[]{"CSCI", "ANTHC"}));

            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
