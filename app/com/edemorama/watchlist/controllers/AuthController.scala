package com.edemorama.watchlist.controllers

import com.edemorama.watchlist._
import com.edemorama.watchlist.services.AuthService
import com.google.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthController @Inject()(cc: ControllerComponents,
                               as: AuthService) extends AbstractController(cc) {

  val logger =  Logger("AuthController")

  def authorise(custId: CustId): Action[AnyContent] = Action.async { implicit req =>
    as.authorise(custId)
      .map(_.fold(missingBadRequest(custId))(wl => Ok(Json.toJson(wl))))
      .recover(getError(custId: CustId))
  }

  def unauthorise(custId: CustId): Action[AnyContent] = Action.async { implicit req =>
    as.unauthorise(custId)
      .map(_.fold(missingBadRequest(custId))(wl => Ok(Json.toJson(wl))))
      .recover(getError(custId: CustId))
  }

  private def missingBadRequest(custId: String) = badRequestResult(s"there is no customer with id $custId.", logger)
  private def getError(custId: String) = internalErrorResult(s"Unable to authorise customer with id $custId.", logger)

}
