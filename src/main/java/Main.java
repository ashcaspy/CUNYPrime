
import java.io.IOException;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.*;
import cunyfirst.CunyFirstClient;
import cunyfirst.ID;

import parser.Course;
import parser.Parser;
import parser.Section;

import java.sql.*;

class Main {
        public static void main(String[] args) {
            if(args.length != 2) {
                return;
            }

            CunyFirstClient wc = new CunyFirstClient();

            /*
             * ASHLEY
             */
            Connection conn;
            String serverName = "localhost:3306";
            String mydatabase = "cunyfirst";
            String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            try {
                conn = DriverManager.getConnection(url, args[0], args[1]);
                Course.createTable(conn);
                Section.createTable(conn);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            wc.retrieve("Hunter College", "Fall", 2015, null, Arrays.asList(new String[]{"CSCI", "ANTHC"}), conn);

            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
