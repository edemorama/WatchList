package com.edemorama.watchlist.controllers

import com.edemorama.watchlist._
import com.edemorama.watchlist.services.WatchListService
import javax.inject._
import play.api._
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class WatchListController @Inject()(cc: ControllerComponents,
                                    wls: WatchListService) extends AbstractController(cc) {

  private val logger =  Logger("WatchListController")

  def get(token: AuthToken): Action[AnyContent] = Action.async { implicit req =>
    wls.get(token)
      .map(_.fold(missingBadRequest(token))(wl => Ok(Json.toJson(wl))))
      .recover(getError(token))
  }

  def add(token: AuthToken): Action[JsValue] = Action(parse.json).async { implicit request =>
    logger.debug(s"Adding watchlist to customer id $token" )
    parseAndProcessJsonBody(token, request)(wls.add)
  }

  def delete(token: AuthToken): Action[JsValue] = Action(parse.json).async { request =>
    logger.debug(s"deleting watchlist from customer id $token" )
    parseAndProcessJsonBody(token, request)(wls.delete)
  }

  private def parseAndProcessJsonBody(token: AuthToken, request: Request[JsValue])
                                     (wlsFun: (String, WatchList) => Future[Option[WatchList]]) = {
    request.body.validate[WatchList].fold(
      jsErrors => {
        logger.error(jsErrors.mkString)
        Future.successful(BadRequest(JsError.toJson(jsErrors)))
      },
      wl => {
        logger.debug(s"watchlist parsed for customer id $token")
        wlsFun(token, wl)
          .map(_.fold(missingBadRequest(token))(wlOut => Ok(Json.toJson(wlOut))))
          .recover(getError(token))
      }
    )
  }

  private def missingBadRequest(String: String) = badRequestResult(s"not authorised with token $String.", logger)
  private def getError(String: String) = internalErrorResult(s"Unable to view watchlist with token $String", logger)
}
