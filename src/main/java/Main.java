
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.*;
import cunyfirst.CunyFirstClient;

class Main {
        public static void main(String[] args) throws IOException {
            CunyFirstClient wc = new CunyFirstClient();
            wc.setup("Hunter College", "2015 Fall Term");

            List<HtmlOption> depts = wc.getSelect(cunyfirst.ID.selectDept).getOptions();

            //the first option is blank
            HashMap<String, HtmlPage> departments = new HashMap<>(depts.size()-1);
            for(HtmlOption o: depts.subList(1,depts.size())) {
                String deptCode = o.getValueAttribute();
                departments.put(deptCode, wc.getResults(deptCode));
            }
            //System.out.println(wc.getResults("CSCI").asXml());
        }

}
