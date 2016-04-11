package controllers

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import models.Consumer
import play.api.Logger
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.mvc.BodyParsers
import play.api.libs.json._
import com.wordnik.swagger.annotations._

/**
 * Controller for Consumer
 */
object Application extends Controller {

  /**
   * Check whether Application is up or not
   */
  def index = Action {
    Ok("Application is up and running!!")
  }

  /**
   * Endpoint for Swagger
   */
  def swagger = Action {
    request =>
      Ok(views.html.swagger())
  }

 
}