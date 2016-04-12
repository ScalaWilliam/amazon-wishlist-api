package models


import java.net.URI
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.agent.Agent
import com.scalawilliam.wishlist.extraction.WishlistFetcherPath
import com.scalawilliam.wishlist.model.clean.CleanWishlist
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.inject.ApplicationLifecycle
import play.api.libs.ws.WSClient

import scala.concurrent.{Future, ExecutionContext}

/**
  * Created on 08/07/2015.
  */
@Singleton
class PlayWishlistManager @Inject()
(applicationLifecycle: ApplicationLifecycle, wsClient: WSClient)
(implicit executionContext: ExecutionContext, actorSystem: ActorSystem) {

  val wishlist = Future(concurrent.blocking(fetchWishlist))

  val defaultWishlistId = "1PZHU4HY3MXLI"

  val path = WishlistFetcherPath(defaultWishlistId)

  def fetchWishlist: CleanWishlist = {
    def fetchPages(uri: URI): List[(URI, Document)] = {
      val document = Jsoup.parse(uri.toURL, 10000)
      path.getNextUri(document) match {
        case None => List(uri -> document)
        case Some(nextUri) => List(uri -> document) ++ fetchPages(nextUri)
      }
    }

    CleanWishlist.fromFetchedObjects(fetchPages(path.entryUri)).get
  }

}
