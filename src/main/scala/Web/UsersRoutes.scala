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

    // TODO - Part 3 Step 3a: Display a login form and register form page for the following URL: `/login`.

    @cask.get("/login")
    def login() =
      Layouts.login(StatusCode.OK)

    // TODO - Part 3 Step 3b: Process the login information sent by the form with POST to `/login`,
    //      set the user in the provided session (if the user exists) and display a successful or
    //      failed login page.

    @getSession(sessionSvc)
    @cask.postForm("/login")
    def postLogin(username: String)(session: Session) =
      println("")
      if (accountSvc.isAccountExisting(username)) {
        session.setCurrentUser(username)
        Layouts.successPage
//        cask.Redirect("/success")
      }
      else {
        Layouts.login(StatusCode.LoginError)
      }
    end postLogin

    // TODO - Part 3 Step 3c: Process the register information sent by the form with POST to `/register`,
    //      create the user, set the user in the provided session and display a successful
    //      register page.

    @getSession(sessionSvc)
    @cask.postForm("/register")
    def postRegister(username: String)(session: Session) =
      if (accountSvc.isAccountExisting(username)) {
        Layouts.login(StatusCode.RegisterError)
      }
      else {
        accountSvc.addAccount(username, accountSvc.defaultBalance)
        session.setCurrentUser(username)
        Layouts.successPage
//        cask.Redirect("/success")
      }
    end postRegister

    // TODO - Part 3 Step 3d: Reset the current session and display a successful logout page.


//    @getSession(sessionSvc)
//    @cask.get("/success")
//    def success()(session: Session) =
//      Layouts.successPage
//    end success

    initialize()
end UsersRoutes
