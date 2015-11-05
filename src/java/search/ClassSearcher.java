package search;


import java.io.IOException;
import java.sql.DriverManager;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.*;
import search.cunyfirst.CunyFirstClient;
import search.cunyfirst.ID;

import search.parser.Parser;

import java.sql.*;

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
                Statement st = conn.createStatement();
                st.executeUpdate("CREATE TABLE IF NOT EXISTS courses(" +
                        "dept varchar(6)," +
                        "nbr varchar(7)," +
                        "name varchar(90)," +
                        "components varchar(40)," +
                        "PRIMARY KEY(dept, nbr)" +
                        ")");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS sections(" +
                        "cdept varchar(6)," +
                        "cnbr varchar(7)," +
                        "sec varchar(20)," +
                        "starttime varchar(22)," +
                        "endtime varchar(22)," +
                        "days varchar(21)," +
                        "room varchar(40)," +
                        "instructor varchar(200)" +
                        ")");
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            //the first option is blank
            for (HtmlOption o : depts.subList(1, depts.size())) {
                try {
                String deptCode = o.getValueAttribute();
                if(ID.skippedDepts.contains(deptCode)) {
                    continue;
                }
                new Parser(wc, wc.getResults(deptCode)).addToTable(conn);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
