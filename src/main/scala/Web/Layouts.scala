// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 09.05.22

package Web

import scalatags.Text.all.*
import scalatags.Text.tags2.nav

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
      body(
        nav(
          a(cls := "nav-brand","Bot-tender"),
          div(cls := "nav-item",
            a(href := "/login", "Log in"),
          )
        ),
        div(cls := "content",
          div(id := "boardMessage",
            div(cls := "msg",
              span(cls := "author"),
              div(cls := "msg-content", "Please wait, the messages are loading !")
            )
          ),
          form(id := "msgForm", onsubmit := "submitMessageForm();return false",
            div(id := "errorDiv", cls := "errorMsg"),
            label(`for` := "messageInput", "Your message: "),
            input(`type` := "text", id := "messageInput", placeholder := "Write your message"),
            input(`type` := "submit", value := "Envoyer")
          )
        )
      )
    )
  }
end Layouts
