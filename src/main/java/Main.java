
import java.io.IOException;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.*;
import cunyfirst.CunyFirstClient;
import cunyfirst.ID;

import parser.Parser;

import java.sql.*;

class Main {
        public static void main(String[] args) throws IOException {
            CunyFirstClient wc = new CunyFirstClient();
            wc.setup("Hunter College", ID.semester("Fall", 2015));

            List<HtmlOption> depts = wc.getSelect(cunyfirst.ID.selectDept).getOptions();

            //the first option is blank
            HashMap<String, Parser> departments = new HashMap<>(depts.size()-1);
            for(HtmlOption o: depts.subList(1,depts.size())) {
                String deptCode = o.getValueAttribute();
                if(ID.skippedDepts.contains(deptCode)) {

                    continue;
                }
                departments.put(deptCode, new Parser(wc.getResults(deptCode).asXml()));
            }

            try {
                String serverName = "localhost:3306";
                String mydatabase = "cunyfirst";
                String url = "jdbc:mysql://" + serverName + "/" + mydatabase;

                Connection conn = DriverManager.getConnection(url, args[0], args[1]);

                for(Parser p: departments.values()) {
                    p.addToTable(conn);
                }

                conn.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
}
