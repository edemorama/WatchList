package com.edemorama.watchlist.services

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.edemorama.watchlist.services.InMemoryServiceActor.{Add, Delete, GetWatchList}
import com.edemorama.watchlist.WatchList
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton
class WatchListService @Inject()(@Named(InMemoryServiceActor.name) imsActor: ActorRef) extends ContentService {
  implicit private val timeout: Timeout = 2.seconds

  def add(token: String, contents: WatchList): Future[Option[WatchList]] = (imsActor ? Add(token, contents)).mapTo[Option[WatchList]]

  def delete(token: String, contents: WatchList): Future[Option[WatchList]] = (imsActor ? Delete(token, contents)).mapTo[Option[WatchList]]

  def get(token: String): Future[Option[WatchList]] = (imsActor ? GetWatchList(token)).mapTo[Option[WatchList]]
}


trait ContentService {
  def add(token: String, contents: WatchList): Future[Option[WatchList]]
  def delete(token: String, contents: WatchList): Future[Option[WatchList]]
  def get(token: String): Future[Option[WatchList]]
}