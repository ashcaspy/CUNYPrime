
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.*;
import cunyfirst.CunyFirstClient;
import cunyfirst.ID;

import parser.Parser;

class Main {
        public static void main(String[] args) throws IOException {
            CunyFirstClient wc = new CunyFirstClient();
            wc.setup("Hunter College", ID.semester("Fall", 2015));

            List<HtmlOption> depts = wc.getSelect(cunyfirst.ID.selectDept).getOptions();

            //the first option is blank
            HashMap<String, Parser> departments = new HashMap<>(depts.size()-1);
            for(HtmlOption o: depts.subList(1,depts.size())) {
                String deptCode = o.getValueAttribute();
                departments.put(deptCode, new Parser(wc.getResults(deptCode).asXml()));
            }
            //Parser p = new Parser(wc.getResults("CSCI").asXml());
            System.out.println("CSCI 12100: 4 sections");
            departments.get("CSCI").get("CSCI 12100").stream().forEach(System.out::println);
        }
}
