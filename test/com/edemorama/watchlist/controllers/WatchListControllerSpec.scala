package com.edemorama.watchlist.controllers

import com.edemorama.watchlist.{CustId, WatchList, errorJson}
import com.edemorama.watchlist.common.TestData._
import com.edemorama.watchlist.services.{AuthService, WatchListService}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.mvc.Result
import play.api.test.{FakeRequest, _}
import play.api.test.Helpers._

import scala.concurrent.{Await, Future}

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class WatchListControllerSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with MockitoSugar {

  implicit val mat = app.materializer

  "WatchListController" when {

    "fulfilling requests for a valid customer" should {

      trait WatchListTests {
        def statusName: String
        def statusValue: Int
        def token: String
        def verifyMocks(method: String) = ()
        val errorMsg500 = "emulated service error"


        val watchListController = inject[WatchListController]
        val authService = inject[AuthService]

        def authoriseTokenById(custId: CustId) = await(authService.authorise(custId)).getOrElse("NOT_AUTHORISED")

        def verifyResults(method: String,result: Future[Result], statusVal: Int, cids: WatchList): Unit = {
          status(result) mustBe statusVal
          statusVal match {
            case OK => contentAsJson(result).as[WatchList] mustBe cids
            case BAD_REQUEST => contentAsJson(result) mustBe errorJson(s"not authorised with token $token.")
            case INTERNAL_SERVER_ERROR =>
              contentAsJson(result) mustBe errorJson(s"Unable to view watchlist with token $token - $errorMsg500")
          }
          verifyMocks(method)
        }

        def runTests {
          s"return a $statusName status when adding content" in {
            val result = watchListController.add(token)(
              FakeRequest().withMethod("POST").withBody(cIDsToAddJs)
            )
            verifyResults("ADD",result, statusValue, cIDsToAdd)
          }

          s"return a $statusName status when deleting content" in {
            val result = watchListController.delete(token)(
              FakeRequest().withMethod("POST").withBody(cIDsToDeleteJs)
            )
            verifyResults("DELETE", result, statusValue, cIDsAfterDelete)
          }

          s"return a $statusName status when viewing" in {
            val result = watchListController.get(token)(FakeRequest())
            verifyResults("GET", result, statusValue, cIDsAfterDelete)
          }
        }
      }

      new WatchListTests {
        val statusName = "OK"
        val statusValue = OK
        val token = authoriseTokenById("123")

      } runTests

      new WatchListTests {
        val statusName = "BAD_REQUEST"
        val statusValue = BAD_REQUEST
        def token = "abc"
      } runTests

      new WatchListTests {
        val statusName = "INTERNAL_SERVER_ERROR"
        val statusValue = INTERNAL_SERVER_ERROR
        def token = authoriseTokenById("123")

        override def verifyMocks(method: String) = {
          method match {
            case "ADD" => verify(mockWatchListService).add(token, cIDsToAdd)
            case "DELETE" => verify(mockWatchListService).delete(token, cIDsToDelete)
            case "GET" => verify(mockWatchListService).get(token)
          }
        }

        val emulatedError = new RuntimeException(errorMsg500)

        val mockWatchListService = mock[WatchListService]
        when(mockWatchListService.get(anyString())).thenReturn(Future.failed(emulatedError))
        when(mockWatchListService.add(anyString(), any[WatchList])).thenReturn(Future.failed(emulatedError))
        when(mockWatchListService.delete(anyString(), any[WatchList])).thenReturn(Future.failed(emulatedError))

        override val watchListController = new WatchListController(stubControllerComponents(), mockWatchListService)
      } runTests
    }
  }
}
