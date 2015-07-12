package com.scalawilliam.wishlist.manager

import akka.actor.ActorSystem
import akka.agent.Agent
import com.scalawilliam.util.MVStoreAsyncHttpCache
import com.scalawilliam.wishlist.extraction.WishlistFetcher
import com.scalawilliam.wishlist.model.clean.CleanWishlist
import org.json4s.DefaultFormats

import scala.async.Async
import scala.concurrent.{ExecutionContext, Future}

case class WishlistId(wishlistId: String)

object WishlistId {
  def myWishlistId = WishlistId("1PZHU4HY3MXLI")
}

case class WishlistManager
(
  wishlistId: WishlistId,
  httpCache: MVStoreAsyncHttpCache,
  agt: Agent[Option[CleanWishlist]]
) {

  implicit val jFormats = DefaultFormats

  def fetchWishlist(implicit executionContext: ExecutionContext, system: ActorSystem): Future[Option[CleanWishlist]] = {
    Async.async {
      agt.get() match {
        case None => Async.await(fetchCleanWishlist)
        case other => other
      }
    }
  }

  def fetchCleanWishlist(implicit executionContext: ExecutionContext, system: ActorSystem): Future[Option[CleanWishlist]] = {
    WishlistFetcher(wishlistId, httpCache.receive).fetchOptions.fetch.map(CleanWishlist.fromFetchedObjects).map { item =>
      item.toOption.foreach{ stuff => agt.send(Option(stuff) )}
      item.toOption
    }
  }
}
