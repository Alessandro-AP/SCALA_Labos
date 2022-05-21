// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 09.05.22

package Web

import Data.{AccountService, SessionService, Session}
import Utils.StatusCode

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

    @getSession(sessionSvc)
    @cask.get("/login")
    def login()(session: Session) =
      Layouts.login(StatusCode.OK, session.getCurrentUser)

    @getSession(sessionSvc)
    @cask.postForm("/login")
    def postLogin(username: String)(session: Session) =
      println("")
      if (accountSvc.isAccountExisting(username)) {
        session.setCurrentUser(username)
        Layouts.successPage(session.getCurrentUser)
//        cask.Redirect("/success")
      }
      else {
        Layouts.login(StatusCode.LoginError, session.getCurrentUser)
      }
    end postLogin

    @getSession(sessionSvc)
    @cask.postForm("/register")
    def postRegister(username: String)(session: Session) =
      if (accountSvc.isAccountExisting(username)) {
        Layouts.login(StatusCode.RegisterError, session.getCurrentUser)
      }
      else {
        accountSvc.addAccount(username, accountSvc.defaultBalance)
        session.setCurrentUser(username)
        Layouts.successPage(session.getCurrentUser)
//        cask.Redirect("/success")
      }
    end postRegister

    @getSession(sessionSvc)
    @cask.get("/logout")
    def logout()(session: Session) =
      if(session.getCurrentUser.isDefined)
        session.reset()
      Layouts.successPage(session.getCurrentUser)

  //    @getSession(sessionSvc)
//    @cask.get("/success")
//    def success()(session: Session) =
//      Layouts.successPage
//    end success

    initialize()
end UsersRoutes
