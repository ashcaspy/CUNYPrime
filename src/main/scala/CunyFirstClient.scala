import com.gargoylesoftware.htmlunit._
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html._
import java.net.URL
import com.gargoylesoftware.htmlunit.util.NameValuePair
import collection.JavaConversions.{asScalaBuffer, seqAsJavaList}

class CunyFirstClient extends WebClient {
  val url = new URL("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/GUEST/HRMS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL")

  this.getOptions setCssEnabled false
  this.getOptions setJavaScriptEnabled true
  this.getOptions setThrowExceptionOnScriptError false
  this.getCookieManager setCookiesEnabled true
  this setIncorrectnessListener Silent

  val settings = new WebRequest(url, HttpMethod.POST)
  val page: HtmlPage = getPage(settings)

  //result pages don't have Selects
  def getSelect(id: String) = (page getElementById id).asInstanceOf[HtmlSelect]

  private lazy val school = getSelect("CLASS_SRCH_WRK2_INSTITUTION$42$")
  private lazy val term = getSelect("CLASS_SRCH_WRK2_STRM$45$")

  def dept = getSelect("SSR_CLSRCH_WRK_SUBJECT_SRCH$0")

  //set institution, term, and course number
  def setup(inst: String, semester: String) = {
    school.setSelectedAttribute(school.getOptionByText(inst), true)
    waitForBackgroundJavaScript(10000)
    term.setSelectedAttribute(term.getOptionByText(semester), true)
    waitForBackgroundJavaScript(10000)

    //has to be done after school and term are set
    //search for course numbers > 0
    val exact = getSelect("SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1")
    exact setSelectedAttribute(exact getOptionByValue "G", true)
    (page getElementById "SSR_CLSRCH_WRK_CATALOG_NBR$1").asInstanceOf[HtmlTextInput] setText "0"
  }

  def params = (asScalaBuffer(((page.getForms get 0) getWebRequest null).getRequestParameters) map (a => (a.getName, a.getValue)))
    .toMap.updated("ICAction", "CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH").updated("SSR_CLSRCH_WRK_SSR_OPEN_ONLY$chk$5", "N")

  def getResults(updatedSettings: java.util.List[NameValuePair]): HtmlPage = {
    settings.setRequestParameters(updatedSettings)
    getPage(settings)
  }
}

object Silent extends IncorrectnessListener {
  def notify(x1: String, x2: Object) = {}
}