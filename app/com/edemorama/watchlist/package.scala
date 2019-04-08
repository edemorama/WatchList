package com.edemorama

import com.edemorama.watchlist.models.ContentID
import play.api.Logger
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import play.api.mvc.Results._

package object watchlist {
  type WatchList = Set[ContentID]
  type CustId = String
  type ContentId = String
  type AuthToken = String

  def errorJson(errorMessage: String) : JsObject = Json.obj("error" -> errorMessage)

  case class MissingCustomerException(msg: String) extends RuntimeException(msg)

  def badRequestResult(errMsg: String, logger: Logger) = {
    logger.error(errMsg)
    BadRequest( errorJson(errMsg))
  }

  def internalErrorResult(msg: String, logger: Logger): PartialFunction[Throwable, Result] = {
    case t: Exception =>
      val errMsg = s"$msg - ${t.getMessage}"
      logger.error(errMsg)
      InternalServerError(errorJson(errMsg))
  }
}
