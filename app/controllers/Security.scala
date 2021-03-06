package jousse
package controllers

import play.api._
import controllers._
import play.api.mvc._
import play.api.data._

import views._
import jousse.models._


object Login extends Controller  {

  // -- Authentication

  val loginForm = new jousse.form.LoginForm form

  /**
   * Login page.
   */
  def form = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(jousse.controllers.routes.Blog.admin).withSession("username" -> user._1)
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(jousse.controllers.routes.Login.form).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

}

/**
 * Provide security features
 */
trait Secured {

  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("username")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(jousse.controllers.routes.Login.form).withNewSession.flashing(
          "error" -> "You have to be logged in to access this page"
        )
  // --

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}


