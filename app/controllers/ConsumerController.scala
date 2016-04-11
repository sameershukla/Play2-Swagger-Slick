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
@Api(value = "/consumer", description = "Operations for consumers")
object ConsumerController extends Controller {

  /**
   * JSON Writes for Rendering JSON Response
   */
  implicit val consumerResponse = Json.writes[Consumer]
  implicit val consumerFormat = Json.format[Consumer]

  /**
   * Read creates a Consumer object out of Json
   */
  implicit val consumerRead: Reads[Consumer] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "name").read(minLength[String](3)) and
    (JsPath \ "lastName").read(minLength[String](5)) and
    (JsPath \ "gender").read(minLength[String](4)) and
    (JsPath \ "email").read(email keepAnd minLength[String](15)) and
    (JsPath \ "password").read(minLength[String](10)) and
    (JsPath \ "zip").read(minLength[String](5)) and
    (JsPath \ "phone").read(minLength[String](15)))(Consumer.apply _)

  /**
   * List all the consumers in the system
   */
  @ApiOperation(nickname = "List", value = "List", notes = "Endpoint for listing all the consumers", httpMethod = "GET")
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Successull Json Response"), new ApiResponse(code = 200, message = "Empty Json Array, if no Consumer Found"), new ApiResponse(code = 500, message = "DB connection error")))
  def list = Action {
    val consumers = Consumer.list
    Ok(Json.obj("consumers" -> consumers))
  }

  /**
   * Find Consumer By Id
   */
  @ApiOperation(nickname = "FindConsumerById", value = "/{id}", notes = "Endpoint for Finding a consumer byId", httpMethod = "GET")
  @ApiResponses(Array(new ApiResponse(code = 200, message = "successful Json Response"), new ApiResponse(code = 404, message = "Consumer Not Found"), new ApiResponse(code = 500, message = "DB connection error")))
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "id", value = "Id on which we need to find Consumer", required = true, dataType = "integer", paramType = "path")))
  def byId(id: Long) = Action {
    val consumer = Consumer.byId(id)

    //TODO: Replace it with Fold
    consumer match {
      case Some(x) => Ok(Json.obj("consumer" -> consumer))
      case None    => NotFound("Consumer Not Found")
    }
  }

  /**
   * Delete Consumer by Id
   */
  @ApiOperation(nickname = "Delete Consumer", value = "/{id}", notes = "Endpoint for Deleting a consumer byId", httpMethod = "DELETE")
  @ApiResponses(Array(new ApiResponse(code = 200, message = ""), new ApiResponse(code = 404, message = "Consumer Not Exists"), new ApiResponse(code = 500, message = "DB connection error")))
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "id", value = "Delete Consumer based on given Id in the Request", required = true, dataType = "integer", paramType = "path")))
  def delete(id: Long) = Action {
    val consumer = Consumer.byId(id)
    consumer match {
      case Some(x) => Consumer.delete(id); Ok
      case None => NotFound("Consumer Not Found")
    }
  }

  /**
   * Insert Consumer, Uniqueness is on the email of the consumer. If consumer with same email already exists then consumer will not be added.
   * If email is unique then only consumer will be added in the system.
   */
  @ApiOperation(nickname = "createConsumer", value = "Create Consumer", notes = "Endpoint for creating consumer", httpMethod = "POST")
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Consumer with name saved"), new ApiResponse(code = 400, message = "Consumer Already exists"), new ApiResponse(code = 500, message = "DB connection error")))
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "Consumer object that need to be created", required = true, dataType = "models.Consumer", paramType = "body")))
  def insert = Action(parse.json) { request =>
    val consumerResult = request.body.validate[Consumer]
    consumerResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "NO", "message" -> "Invalid Json"))
      },
      consumer => {
        val isExists = Consumer.insertIfNotExists(consumer.email)
        isExists match {
          case Some(x) => NotFound("Consumer with the given email already exists")
          case None => Consumer.add(consumer); Ok(Json.obj("status" -> "OK", "message" -> ("Consumer '" + consumer.name + "' saved.")))
        }
      })
  }

  /**
   * Update Consumer with Id, if no id given in the request it will add record in the system.
   */
  @ApiOperation(nickname = "Update Consumer", value = "update", notes = "Endpoint for Updating a consumer", httpMethod = "PUT")
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Consumer with name updated"), new ApiResponse(code = 404, message = "Consumer not Exists,Not able to update"), new ApiResponse(code = 500, message = "DB connection error")))
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "Consumer object that need to be updated, if no Id is given in the request it will add Consumer in the System", required = true, dataType = "models.Consumer", paramType = "body")))
  def update = Action(parse.json) { request =>
    val consumerResult = request.body.validate[Consumer]
    consumerResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "NO", "message" -> "Invalid Json"))
      },
      consumer => {
        consumer.id match {
          case None => {
            Consumer.add(consumer)
            Ok(Json.obj("status" -> "OK", "message" -> ("Consumer '" + consumer.name + "' saved.")))
          }
          case _ => {
            Consumer.byId(consumer.id.get) match {
              case Some(x) =>
                Consumer.update(consumer); Ok(Json.obj("status" -> "OK", "message" -> ("Consumer '" + consumer.name + "' updated.")))
              case None => NotFound("Consumer not Exists,Not able to update")
            }
          }
        }
      })
  }
}