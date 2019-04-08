package com.edemorama.watchlist.models

import com.edemorama.watchlist.{ContentId, CustId, WatchList}
import play.api.libs.json.Json

//TODO: create types for 3 and 5 digit alphanumerics for constraint purposes

case class ContentID(id: ContentId)
object ContentID {
  implicit val contentFormat = Json.format[ContentID]
}

case class Customer(id: CustId, watchlist: WatchList)
object Customer {
 implicit val customerFormat = Json.format[Customer]
}