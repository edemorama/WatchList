package com.edemorama.watchlist.common

import com.edemorama.watchlist.models.ContentID
import play.api.libs.json.Json

object TestData {
  val cIDsToAdd = Set("zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr").map(str => ContentID(str))
  val cIDsToAddJs = Json.toJson(cIDsToAdd)

  val cIDsToDelete = Set("15nW5", "srT5k", "FBSxr").map(str => ContentID(str))
  val cIDsToDeleteJs = Json.toJson(cIDsToDelete)

  val cIDsAfterDelete = Set("zRE49", "wYqiZ").map(str => ContentID(str))

}
