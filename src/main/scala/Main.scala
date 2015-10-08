import com.gargoylesoftware.htmlunit._
import com.gargoylesoftware.htmlunit.html._
import com.gargoylesoftware.htmlunit.util.NameValuePair
import java.net.URL
import collection.JavaConversions

object Main {
  val url = new URL("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/GUEST/HRMS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL")

  def main(args: Array[String]): Unit = {
    val wc = new CunyFirstClient
    wc.setup("Hunter College", termToString("Fall", 2015))

    val newSettings = mapToList(withDept(wc.params, "CSCI"))

    val res: HtmlPage = wc getResults newSettings
    val csci = new java.io.PrintWriter("csresults.html")
    csci.write(res.asXml)
    csci.close()

    wc getResults mapToList(withDept(wc.params, "ANTHC"))
    val anthc: HtmlPage = wc getPage wc.settings

    val anth = new java.io.PrintWriter("anthcresults.html")
    anth.write(anthc.asXml)
    anth.close()
  }

  def withDept(params: Map[String, String], dept: String): Map[String, String] =
    params updated ("SSR_CLSRCH_WRK_SUBJECT_SRCH", dept)

  def mapToList(m: Map[String, String]) = {
    def toNVPair(p: (String, String)) = new NameValuePair(p._1, p._2)
    JavaConversions.seqAsJavaList((m map toNVPair).toList)
  }

  def termToString(season: String, year: Int) = year.toString + " " + season + " Term"

}
