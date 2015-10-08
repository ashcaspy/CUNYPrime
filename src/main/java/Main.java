
import java.io.IOException;
import java.io.PrintWriter;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

class Main {

        public static void main(String[] args) throws IOException {
            CunyFirstClient wc = new CunyFirstClient();
            wc.setup("Hunter College", "2015 Fall Term");

            HtmlPage res = wc.getResults("CSCI");
            PrintWriter file = new PrintWriter("csresults.html");
            file.write(res.asXml());
            file.close();

            res = wc.getResults("ANTHC");
            file = new PrintWriter("anthcresults.html");
            file.write(res.asXml());
            file.close();
        }

}
