package search;


import java.io.IOException;
import java.sql.DriverManager;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.*;
import search.cunyfirst.CunyFirstClient;
import search.cunyfirst.ID;


import java.sql.*;
import java.util.Arrays;
import search.cunyfirst.MatchValuePair;
import search.cunyfirst.TimeRange;
import search.parser.Course;
import search.parser.Section;

class ClassSearcher {
        public static void searchClasses(String[] args) {
            if(args.length != 2) {
                return;
            }

            CunyFirstClient wc = new CunyFirstClient();
            wc.setup("Hunter College", ID.semester("Fall", 2015));

            List<HtmlOption> depts = wc.getSelect(search.cunyfirst.ID.selectDept).getOptions();
            Connection conn;
            
            String url = "postgres://dohlisjkikwpju:P8E-Lh7jMSEUfyQb5RrR4m-fEJ@ec2-107-21-219-235.compute-1.amazonaws.com:5432/dejqt9ki5rgaao";

            
            //String serverName = "ec2-107-21-219-235.compute-1.amazonaws.com";
            //String mydatabase = "cunyfirst";
            //String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            
            //String serverName = "localhost:3306";
            //String mydatabase = "cunyfirst";
            //String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            try {
                conn = DriverManager.getConnection(url, args[0], args[1]);
                Course.createTable(conn);
                Section.createTable(conn);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            wc.retrieve("Hunter College", "Fall", 2015,
                    new MatchValuePair(ID.greaterThan, "0"), new TimeRange(10, 12), new TimeRange(11, 14), null, null,
                    new int[] {3},
                    Arrays.asList(new String[]{"CSCI", "ANTHC"}), conn);

            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
