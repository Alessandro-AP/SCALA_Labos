// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 24.05.22

package Web

/**
  * Assembles the routes dealing with static files.
  * This class allows our static files (`.js` and `.css`) to be accessed from the browser.
  */
class StaticRoutes()(implicit val log: cask.Logger) extends cask.Routes:

    /**
      * Serve .js files.
      */
    @cask.staticResources("/static/js" )
    def staticResourcesJs() = "./js"

    /**
      * Serve .css files.
      */
    @cask.staticResources("/static/css")
    def staticResourcesCss() = "./css"

    initialize()
end StaticRoutes
