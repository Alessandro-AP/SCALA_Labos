// SCALA - Labo 4
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 19.06.22

package Web

import Services.MessageService
import Services.MessageService.{MsgContent, Username}
import scalatags.Text
import scalatags.Text.all.*
import scalatags.Text.tags2.nav
import Utils.StatusCode

/**
  * Assembles the method used to layout ScalaTags.
  * 
  * This class contains the views and components of our HTML pages.
  */
object Layouts:

  type HtmlTag = Text.TypedTag[String]

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
  
  private def navBarLayout(username: Option[String], link : Option[Frag]) = {
    nav(
      a(cls := "nav-brand")("Bot-tender"),
      div(cls := "nav-item")(
        username.map(u =>
          div(p(display := "inline", margin := "6px")(s"Hello $u"),a(href := "/logout")("Logout"))
        ).getOrElse(link.getOrElse(a(href := "/login")("Log in"))),
      )
    )
  }

  private def pageLayout(content: Text.TypedTag[String], username : Option[String], link : Option[Frag] = None) = {
    html(
      headLayout,
      body(
        navBarLayout(username, link),
        content
      )
    )
  }

  def homepage(username : Option[String], messages: Seq[(Username, MsgContent)] = Seq.empty): HtmlTag = {
    pageLayout(homepageContent(messages), username)
  }

  def msgContent(msg: String): Frag = {
    span(cls := "msg-content")(msg)
  }

  def msgList(messages: Seq[(Username, MsgContent)]): Frag = {
    frag(
      for ((author, msg) <- messages)
      yield
        div(cls := "msg")(
          span(cls := "author")(author),
          msg
        )
    )
  }

  private def homepageContent(messages: Seq[(Username, MsgContent)]) = {
    div(cls := "content")(
      div(id := "boardMessage")(
        div(cls := "msg")(
          if messages.isEmpty then
            span(cls := "msg-content")("No messages have been sent yet !")
          else
            msgList(messages)          
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

  def login(statusCode: StatusCode): HtmlTag = {
    pageLayout(loginContent(statusCode), None, Some(a(href := "/")("Back to chat")))
  }

  private def loginContent(statusCode: StatusCode) = {
    div(cls := "content")(
      div(
        p(cls := "title")("Login"),
        form(id := "loginForm", action := "/login", method := "post")(
          errorSection(statusCode, StatusCode.LoginError, "User not found !"),
          loginInputs
        )
      ),
      div(
        p(cls := "title")("Register"),
        form(id := "registerForm", action := "/register", method := "post")(
          errorSection(statusCode, StatusCode.RegisterError, "Username not valid or already exists, please choose another username !"),
          loginInputs
        )
      )
    )
  }

  private def loginInputs = {
    frag(
      label(`for` := "usernameInput")("Username: "),
      input(`type` := "text", id := "usernameInput", name := "username", placeholder := "Write your username"),
      input(`type` := "submit", value := "Envoyer")
    )
  }

  private def errorSection(statusCode: StatusCode, checkCode: StatusCode, errorMsg: String) = {
    if (statusCode == checkCode)
      div(id := "errorDiv", cls := "errorMsg")(errorMsg)
    else
      div(display := "none") // doesn't work without else
  }

  def successPage(username: Option[String] = None): HtmlTag = {
    pageLayout(
      div(cls := "content", cls := "success")(
        p(cls := "title")("Bienvenue sur Bot-tinder, appuyez sur le bouton ci-dessous pour retourner au Chat !"),
        a(href := "/", cls := "button")("Back to Chat")
      ),
      username
    )
  }

end Layouts
