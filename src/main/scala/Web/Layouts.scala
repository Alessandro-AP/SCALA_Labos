// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 09.05.22

package Web

import scalatags.Text.all._
import scalatags.Text.tags2

/**
  * Assembles the method used to layout ScalaTags
  */
object Layouts:
  // You can use it to store your methods to generate ScalaTags.
  def homepage = {
    html(
      head(
        meta(charset := "utf-8"),
        meta(name := "viewport", content := "initial-scale = 1.0,maximum-scale = 1.0"),
        link(
          rel := "stylesheet",
          href := "static/css/main.css"
        ),
        script(src := "static/js/main.js"),

      ),
      body(margin := 0, backgroundColor := "#f8f8f8")(
        div(
          h1(id := "title", "This is a title"),
          p("This is a big paragraph of text")
        )
      )
    )
  }
end Layouts
