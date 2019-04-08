package com.edemorama.watchlist.acceptance

import com.edemorama.watchlist.{AuthToken, WatchList}
import com.edemorama.watchlist.common.TestData._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._


class watchListEndpointSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  "WatchList endpoints" should {

    var token = ""

    "return an authorisation token from the router when authorisiang a customer" in {
      val request = FakeRequest(GET, "/watchlist/authorise?custId=123")
      val eventualResult = route(app, request).get
      status(eventualResult) mustBe OK
      token = contentAsJson(eventualResult).as[AuthToken]
    }

    "return a watchlist from the router when adding content for a valid customer id" in {
      val request = FakeRequest(POST, s"/watchlist/add?authtoken=$token").withBody(cIDsToAddJs)
      val eventualResult = route(app, request).get

      status(eventualResult) mustBe OK
      contentAsJson(eventualResult).as[WatchList] mustBe cIDsToAdd
    }

    "return a watchlist from the router when deleting content for given a valid customer id" in {
      val request = FakeRequest(POST, s"/watchlist/delete?authtoken=$token").withBody(cIDsToDeleteJs)
      val eventualResult = route(app, request).get

      status(eventualResult) mustBe OK
      contentAsJson(eventualResult).as[WatchList] mustBe cIDsAfterDelete
    }

    "return a watchlist from the router when viewing content for a valid customer id" in {
      val request = FakeRequest(GET, s"/watchlist/get?authtoken=$token")
      val eventualResult = route(app, request).get

      status(eventualResult) mustBe OK
      contentAsJson(eventualResult).as[WatchList] mustBe cIDsAfterDelete
    }

    "unauthorise a customer token" in {
      val request = FakeRequest(GET, "/watchlist/unauthorise?custId=123")
      val eventualResult = route(app, request).get
      status(eventualResult) mustBe OK
      contentAsJson(eventualResult).as[AuthToken] mustBe token
    }

    "return not authorised from the router when adding content with an invalid customer token" in {
      val request = FakeRequest(POST, s"/watchlist/add?authtoken=$token").withBody(cIDsToAddJs)
      val eventualResult = route(app, request).get

      status(eventualResult) mustBe BAD_REQUEST
    }
  }

}
