import java.io.PrintWriter

import com.gargoylesoftware.htmlunit._
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html._
import com.gargoylesoftware.htmlunit.util.NameValuePair
import java.net.URL
import collection.JavaConversions

object Main {
  val url = new URL("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/GUEST/HRMS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL")

  def main(args: Array[String]): Unit = {
    val wc = new WebClient
    val options = wc.getOptions
    options setJavaScriptEnabled true
    options setCssEnabled false
    options setThrowExceptionOnScriptError false
    wc.getCookieManager setCookiesEnabled true
    wc setIncorrectnessListener Silent

    val rqsettings = new WebRequest(url, HttpMethod.POST)
    val page: HtmlPage = wc getPage rqsettings

    val inst = (page getElementById "CLASS_SRCH_WRK2_INSTITUTION$42$").asInstanceOf[HtmlSelect]
    inst.setSelectedAttribute(inst getOptionByText "Hunter College", true)

    wc waitForBackgroundJavaScript 5000*10
    val term = (page getElementById "CLASS_SRCH_WRK2_STRM$45$").asInstanceOf[HtmlSelect]
    //check if term options updated
    //println(term.getOptions)

    term setSelectedAttribute (term getOptionByText "2015 Fall Term", true)

    wc waitForBackgroundJavaScript 5000*10
    val dept = (page getElementById "SSR_CLSRCH_WRK_SUBJECT_SRCH$0").asInstanceOf[HtmlSelect]
    //println(dept.getOptions)

    dept setSelectedAttribute (dept getOptionByValue "CSCI", true)

    wc waitForBackgroundJavaScript 5000*10

    val exact_match = (page getElementById "SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1").asInstanceOf[HtmlSelect]
    exact_match setSelectedAttribute (exact_match getOptionByValue "G", true)

    (page getElementById "SSR_CLSRCH_WRK_CATALOG_NBR$1").asInstanceOf[HtmlTextInput] setText "0"
    val params = ((page.getForms get 0) getWebRequest null).getRequestParameters
    val mapParams = (JavaConversions.asScalaBuffer(params) map (a => (a.getName, a.getValue))).toMap
      .updated("ICAction", "CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH").updated("SSR_CLSRCH_WRK_SSR_OPEN_ONLY$chk$5", "N")

    val newSettings = mapToList(mapParams)

    rqsettings setRequestParameters newSettings
    val res: HtmlPage = wc getPage rqsettings
    val csci = new java.io.PrintWriter("csresults.html")
    csci.write(res.asXml)
    csci.close()

    rqsettings setRequestParameters mapToList(withDept(mapParams, "ANTHC"))
    val anthc: HtmlPage = wc getPage rqsettings

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


}

object Silent extends IncorrectnessListener {
  def notify(x1: String, x2: Object) = {}
}