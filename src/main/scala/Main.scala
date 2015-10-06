import com.gargoylesoftware.htmlunit._
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html._
import java.net.URL


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
    println("Page...")
    val page: HtmlPage = wc getPage rqsettings

    val inst = (page getElementById "CLASS_SRCH_WRK2_INSTITUTION$42$").asInstanceOf[HtmlSelect]
    inst.setSelectedAttribute(inst getOptionByText "Hunter College", true)

    wc waitForBackgroundJavaScript 5000
    val term = (page getElementById "CLASS_SRCH_WRK2_STRM$45$").asInstanceOf[HtmlSelect]
    //check if term options updated
    println(term.getOptions)

    term setSelectedAttribute (term getOptionByText "2015 Fall Term", true)

    wc waitForBackgroundJavaScript 5000
    val opt = (page getElementById "SSR_CLSRCH_WRK_SUBJECT_SRCH$0").asInstanceOf[HtmlSelect]
    println(opt.getOptions)

    println("Done")
  }

}

object Silent extends IncorrectnessListener {
  def notify(x1: String, x2: Object) = {}
}