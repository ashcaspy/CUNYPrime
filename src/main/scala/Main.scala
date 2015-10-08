package cunyfirst

object Main {

  def main(args: Array[String]): Unit = {
    val wc = new CunyFirstClient
    wc.setup("Hunter College", termToString("Fall", 2015))

    val res = wc getNewResults "CSCI"
    val csci = new java.io.PrintWriter("csresults.html")
    csci.write(res.asXml)
    csci.close()

    val anthc = wc getNewResults "ANTHC"

    val anth = new java.io.PrintWriter("anthcresults.html")
    anth.write(anthc.asXml)
    anth.close()
  }

  def termToString(season: String, year: Int) = year.toString + " " + season + " Term"
}
