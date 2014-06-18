package controllers

import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.BodyParser
import play.api.mvc.BodyParsers
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results.BadRequest
import play.api.libs.json.Json
import scala.Some

object ControllerHelpers {

  
  def withExceptionHandling[A](f: (Request[A]) => Result)(implicit request: Request[A]): Result = 
    try{ f(request) }catch{ case e: Exception => BadRequest(Json.obj("error" -> e.getMessage()))}

  def ActionWithToken[A](bodyParser: BodyParser[A])(f: (Request[A], String) => Result) = {
    Action(bodyParser) { request =>
      request.headers.get("X-AUTH-TOKEN") match {
        case Some(authToken) => f (request, authToken)
        case None => BadRequest(Json.obj("status" -> "MH", "message" -> "missing X-AUTH-TOKEN http header"))
      }
    }
  }

  def ActionWithoutToken[A](bodyParser: BodyParser[A])(f: (Request[A]) => Result) = {
    Action(bodyParser) { request =>
      f (request)
    }
  }
  

  def ActionWithToken[A](f: (Request[AnyContent], String) => Result) = Action { request =>
    request.headers.get("X-AUTH-TOKEN") match {
      case Some(authToken) => f (request, authToken)
      case None => BadRequest(Json.obj("status" -> "MH", "message" -> "missing X-AUTH-TOKEN http header"))
    }
  }

  def ActionWithoutToken[A](f: (Request[AnyContent]) => Result) = Action { request =>
    f (request)
  }
}
