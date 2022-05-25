// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 24.05.22

package Web

import Services.{AccountService, Session, SessionService}
import Utils.StatusCode
import Web.Layouts.HtmlTag

/**
  * Assembles the routes dealing with the users:
  * - One route to display the login form and register form page
  * - One route to process the login form and display the login success page
  * - One route to process the register form and display the register success page
  * - One route to logout and display the logout success page
  *
  * The username of the current session user is stored inside a cookie called `username`.
  */
class UsersRoutes(accountSvc: AccountService,
                  sessionSvc: SessionService)(implicit val log: cask.Logger) extends cask.Routes:

    import Decorators.getSession

    @cask.get("/login")
    def login(): HtmlTag = {
      Layouts.login(StatusCode.OK)
    }

    @getSession(sessionSvc)
    @cask.postForm("/login")
    def postLogin(username: String)(session: Session): HtmlTag = {
      if (accountSvc.isAccountExisting(username)) {
        session.setCurrentUser(username)
        Layouts.successPage(session.getCurrentUser)
      }
      else {
        Layouts.login(StatusCode.LoginError)
      }
    }

    @getSession(sessionSvc)
    @cask.postForm("/register")
    def postRegister(username: String)(session: Session): HtmlTag = {
      if (accountSvc.isAccountExisting(username)) {
        Layouts.login(StatusCode.RegisterError)
      }
      else {
        accountSvc.addAccount(username, accountSvc.defaultBalance)
        session.setCurrentUser(username)
        Layouts.successPage(session.getCurrentUser)
      }
    }

    @getSession(sessionSvc)
    @cask.get("/logout")
    def logout()(session: Session): HtmlTag = {
      session.reset()
      Layouts.successPage()
    }

    initialize()
end UsersRoutes
