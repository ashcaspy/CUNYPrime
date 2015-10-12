
import java.io.IOException;
import java.sql.DriverManager;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.*;
import cunyfirst.CunyFirstClient;
import cunyfirst.ID;

import parser.Parser;

import java.sql.*;

class Main {
        public static void main(String[] args) {
            if(args.length != 2) {
                return;
            }

            CunyFirstClient wc = new CunyFirstClient();
            wc.setup("Hunter College", ID.semester("Fall", 2015));

            List<HtmlOption> depts = wc.getSelect(cunyfirst.ID.selectDept).getOptions();
            Connection conn;
            String serverName = "localhost:3306";
            String mydatabase = "cunyfirst";
            String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            try {
                conn = DriverManager.getConnection(url, args[0], args[1]);
                Statement st = conn.createStatement();
                st.executeUpdate("CREATE TABLE IF NOT EXISTS courses(" +
                        "dept varchar(6)," +
                        "nbr varchar(7)," +
                        "name varchar(90)," +
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
                new Parser(wc.getResults(deptCode).asXml()).addToTable(conn);
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
