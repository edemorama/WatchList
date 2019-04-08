package com.edemorama.watchlist.services

import java.util.UUID

import akka.actor.{Actor, Props}
import com.edemorama.watchlist.{AuthToken, CustId, WatchList}
import com.edemorama.watchlist.models.Customer
import com.edemorama.watchlist.services.InMemoryServiceActor._
import com.google.inject.Singleton

@Singleton
class InMemoryServiceActor extends Actor {
  var CustomerRepository: Set[Customer] = Set(Customer("123", Set()), Customer("456", Set()), Customer("789", Set()))

  var AuthRepository: Map[CustId, AuthToken] = Map().withDefaultValue("NOT_AUTHORISED")

  def find(custId: CustId): Option[Customer] = CustomerRepository.find(_.id == custId)

  def createToken: AuthToken = {
    UUID.randomUUID().toString.substring(0,8)
  }

  def getCustIdByToken(token: String): Option[CustId] = {
    AuthRepository.find(_._2 == token) map (_._1)
  }

  def getCustomerByToken(token: String): Option[Customer] = {
    for {
      custId <- getCustIdByToken(token)
      cu <- find(custId)
    } yield cu
  }

  def receive: Receive = {
    case Find(custId) =>
      sender() ! find(custId)

    case GetWatchList(token) =>
      sender() ! getCustomerByToken(token).map(_.watchlist)

    case Add(token, contents) =>
      val reply =  getCustomerByToken(token) map { cu =>
        val updatedCu = Customer(cu.id, cu.watchlist ++ contents)
        CustomerRepository = CustomerRepository.filterNot(_.id == cu.id) + updatedCu
        updatedCu.watchlist
      }
      sender() ! reply

    case Delete(token, contents) =>
      val reply = getCustomerByToken(token) map {cu =>
        val updatedCu = Customer(cu.id, cu.watchlist -- contents)
          CustomerRepository = CustomerRepository.filterNot(_.id == cu.id) + updatedCu
          updatedCu.watchlist
      }
      sender() ! reply

    case Auth(custId) =>
       val reply = find(custId) map { cu =>
         if (AuthRepository(custId) == "NOT_AUTHORISED") AuthRepository += (custId -> createToken)
         AuthRepository(custId)
       }
      sender() ! reply

    case UnAuth(custId) =>
      val reply = find(custId) map { cu =>
        val token = AuthRepository(custId)
        if (token != "NOT_AUTHORISED") AuthRepository -= custId
        token
      }
      sender() ! reply
  }
}

object InMemoryServiceActor {
  final val name = "in-memory-actor"
  def props(): Props = Props[InMemoryServiceActor]

  case class Find(custId: CustId)
  case class GetWatchList(custId: CustId)
  case class Add(custId: CustId, contents: WatchList)
  case class Delete(custId: CustId, contents: WatchList)
  case class Auth(custId: CustId)
  case class UnAuth(custId: CustId)
}
