// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 09.05.22

package Web

import Data.MessageService
import scalatags.Text
import scalatags.Text.all.*
import scalatags.Text.tags2.nav
import Utils.StatusCode

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

  private def navBarLayout(username : Option[String]) = {
    nav(
      a(cls := "nav-brand")("Bot-tender"),
      div(cls := "nav-item")(
       if (username.isDefined) div( p(display:= "inline", margin := "6px")(s"Hello ${username.get}"), a(href := "/logout")("Logout"))
       else a(href := "/login")("Log in"),
      )
    )
  }

  private def pageLayout(content: scalatags.Text.Modifier, username : Option[String]) =
    html(
      headLayout,
      body(
        navBarLayout(username),
        content
      )
    )

  def homepage(username : Option[String], msgSvc: MessageService) = {
    pageLayout(homepageContent(msgSvc), username)
  }

  private def homepageContent(msgSvc: MessageService) = {
    div(cls := "content")(
      div(id := "boardMessage")(
        div(cls := "msg")(
          span(cls := "author"),
          span(cls := "msg-content")("Please wait, the messages are loading !") // TODO générer la liste avec msgSvc.getLatestMessages
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

  def login(statusCode: StatusCode, username : Option[String]) = {
    pageLayout(loginContent(statusCode), username)
  }

  private def loginContent(statusCode: StatusCode) = {
    div(cls := "content")(
      div(
        p(cls := "title")("Login"),
        form(id := "loginForm", action := "/login", method := "post")(
          if (statusCode == StatusCode.LoginError)
            div(id := "errorDiv", cls := "errorMsg")("User not found !")
          else div(display := "none"), // doesn't work without else
          label(`for` := "usernameInput")("Username: "),
          input(`type` := "text", id := "usernameInput", name := "username", placeholder := "Write your username"),
          input(`type` := "submit", value := "Envoyer")
        )
      ),
      div(
        p(cls := "title")("Register"),
        form(id := "registerForm", action := "/register", method := "post")(
          if (statusCode == StatusCode.RegisterError)
            div(id := "errorDiv", cls := "errorMsg")("User already exists, choose another username !")
          else div(display := "none"), // doesn't work without else
          label(`for` := "usernameInput")("Username: "),
          input(`type` := "text", id := "usernameInput", name := "username", placeholder := "Write your username"),
          input(`type` := "submit", value := "Envoyer")
        )
      )
    )
  }

  def successPage(username : Option[String]) = {
    pageLayout(
      div(cls := "content", cls := "success")(
        p(cls := "title")("Bienvenue sur Bot-tinder, appuyez sur le bouton ci-dessous pour retourner au Chat !"),
        a(href := "/", cls := "button")("Back to Chat")
      ), username
    )
  }

end Layouts
