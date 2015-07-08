package com.scalawilliam.wishlist.webapp

import java.net.URI

import akka.agent.Agent
import com.scalawilliam.util.MVStoreHttpCache
import com.scalawilliam.wishlist.extraction.WishlistFetcher
import com.scalawilliam.wishlist.model.clean.CleanWishlist
import org.h2.mvstore.MVStore
import org.json4s.DefaultFormats
import scala.async.Async
import scala.concurrent.{ExecutionContext, Future}

trait WishlistManager {
  implicit def executionContext: ExecutionContext

  lazy val mvStore = MVStore.open("main.db")
  lazy val cacheMap = mvStore.openMap[URI, String]("response-bodies")
  lazy val uriFetcher = MVStoreHttpCache(mvStore, cacheMap)
  private lazy val agt = Agent(Option.empty[CleanWishlist])
  implicit val jFormats = DefaultFormats

  def fetchWishlist(): Future[Option[CleanWishlist]] = {
    Async.async {
      agt.get() match {
        case None => Async.await(fetchCleanWishlist())
        case other => other
      }
    }
  }

  def fetchCleanWishlist(): Future[Option[CleanWishlist]] = {
    WishlistFetcher.fetchAmazonUKWishlistPages(fetchURI = uriFetcher)(identity)("1PZHU4HY3MXLI").map(CleanWishlist.fromFetchedObjects).map { item =>
      item.toOption.foreach{ stuff => agt.send(Option(stuff) )}
      item.toOption
    }
  }
}
