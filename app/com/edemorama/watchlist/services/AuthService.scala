package com.edemorama.watchlist.services

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.edemorama.watchlist.{AuthToken, CustId}
import com.edemorama.watchlist.services.InMemoryServiceActor.{Auth, UnAuth}
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named

import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton
class AuthService @Inject()(@Named(InMemoryServiceActor.name) imsActor: ActorRef) {
  implicit private val timeout: Timeout = 2.seconds

  def authorise(custId: CustId): Future[Option[AuthToken]] = (imsActor ? Auth(custId)).mapTo[Option[AuthToken]]

  def unauthorise(custId: CustId): Future[Option[AuthToken]] = (imsActor ? UnAuth(custId)).mapTo[Option[AuthToken]]
}
