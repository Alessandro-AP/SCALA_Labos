// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 09.05.22

package Web

/**
  * Assembles the routes dealing with static files.
  */
class StaticRoutes()(implicit val log: cask.Logger) extends cask.Routes:
    // TODO - Part 3 Step 1: Make the resources files (`.js` and `.css`) available to the browser.
    //      Do not forget to link to them from your HTML.

    @cask.staticResources("/static/js", headers = Seq("Content-Type" -> "text/javascript"))
    def staticFileJs() = "src/main/resources/js"

    @cask.staticResources("/static/css", headers = Seq("Content-Type" -> "text/css"))
    def staticFileCss() = "src/main/resources/css"

    initialize()
end StaticRoutes
