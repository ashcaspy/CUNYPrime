package search;


import java.sql.DriverManager;
import java.util.Arrays;

import search.cunyfirst.*;

import java.sql.*;
import search.cunyfirst.ID;
import search.cunyfirst.MatchValuePair;
import search.cunyfirst.TimeRange;

public class ClassSearcher {
        public static Connection classSearch() {
            

            Connection conn;
            //String serverName = "localhost:3306";
            //String mydatabase = "cunyfirst";
            //String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            String url = "postgres://dohlisjkikwpju:P8E-Lh7jMSEUfyQb5RrR4m-fEJ@ec2-107-21-219-235.compute-1.amazonaws.com:5432/dejqt9ki5rgaao";


            try {
                conn = DriverManager.getConnection(url); // <--- *******
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
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return conn;
        }
}
