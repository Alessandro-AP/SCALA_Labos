// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 09.05.22

package Web

import scalatags.Text
import scalatags.Text.all._
import scalatags.Text.tags2.nav

/**
  * Assembles the method used to layout ScalaTags
  */
object Layouts:
  // You can use it to store your methods to generate ScalaTags.

  private def headLayout = {
    head(
      meta(charset := "utf-8"),
      meta(name := "viewport", content := "initial-scale = 1.0,maximum-scale = 1.0"),
      link(
        rel := "stylesheet",
        href := "static/css/main.css"
      ),
      script(src := "static/js/main.js"),
    )
  }

  private def navBarLayout = {
    nav(
      a(cls := "nav-brand")("Bot-tender"),
      div(cls := "nav-item")(
        a(href := "/login")("Log in"),
      )
    )
  }

  private def pageLayout(content: Text.Modifier) =
    html(
      headLayout,
      body(
        navBarLayout,
        content
      )
    )

  def homepage: Any = {
    pageLayout(homepageContent)
  }

  private def homepageContent = {
    div(cls := "content")(
      div(id := "boardMessage")(
        div(cls := "msg")(
          span(cls := "author"),
          span(cls := "msg-content")("Please wait, the messages are loading !")
        )
      ),
      form(id := "msgForm", onsubmit := "submitMessageForm();return false")(
        div(id := "errorDiv", cls := "errorMsg"),
        label(`for` := "messageInput")("Your message: "),
        input(`type` := "text", id := "messageInput", placeholder := "Write your message"),
        input(`type` := "submit", value := "Envoyer")
      )
    )
  }

  def login: Any = {
    pageLayout(loginPage)
  }

  private def loginPage = {
    div(
      loginContent,
      registerContent
    )
  }

  private def loginContent = {
    div(
      p(fontWeight := "bold")("Login"),
      form(id := "loginForm", onsubmit := "submitLoginForm();return false")(
        div(id := "errorDiv", cls := "errorMsg"),
        label(`for` := "usernameInput")("Username: "),
        input(`type` := "text", id := "usernameInput", placeholder := "Write your username"),
        input(`type` := "submit", value := "Envoyer")
      )
    )
  }

  private def registerContent = {
    div(
      p(fontWeight := "bold")("Register"),
      form(id := "registerForm", onsubmit := "submitRegisterForm();return false")(
        div(id := "errorDiv", cls := "errorMsg"),
        label(`for` := "usernameInput")("Username: "),
        input(`type` := "text", id := "usernameInput", placeholder := "Write your username"),
        input(`type` := "submit", value := "Envoyer")
      )
    )
  }

end Layouts
