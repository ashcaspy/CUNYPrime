import com.gargoylesoftware.htmlunit._
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html._
import java.net.URL
/*
class Web extends WebClient {
  val url = new URL("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/GUEST/HRMS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL")
  //val wc = new WebClient
  def this() = {
    this
    val options = this.getOptions
    options setCssEnabled false
    options setJavaScriptEnabled true
    options setThrowExceptionOnScriptError false
    (this getCookieManager) setCookiesEnabled true
    this setIncorrectnessListener Silent
  }

  val page: HtmlPage = this.getPage(new WebRequest(url, HttpMethod.POST))
}

object Silent extends IncorrectnessListener {
  def notify(x1: String, x2: Object) = {}
}*/