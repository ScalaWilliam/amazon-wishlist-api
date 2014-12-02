package com.scalawilliam.wishlist.webapp

import java.net.URI
import akka.actor.ActorSystem
import akka.util.Timeout
import com.scalawilliam.util.MVStoreHttpCache
import com.scalawilliam.wishlist.extraction.WishlistFetcher
import com.scalawilliam.wishlist.model.SimpleWishlist
import com.scalawilliam.wishlist.model.clean.CleanWishlist
import org.h2.mvstore.MVStore
import org.json4s.DefaultFormats
import spray.http.MediaTypes
import spray.httpx.encoding.Gzip
import spray.routing.SimpleRoutingApp
import akka.actor.ActorDSL._
import akka.pattern.pipe
import akka.pattern.ask


object WishlistApp extends App with SimpleRoutingApp {
  /**
   * http://spray.io/documentation/1.2.1/spray-routing/predefined-directives-alphabetically/
   */
  implicit val system = ActorSystem("my-system")
  lazy val mvStore = MVStore.open("main.db")
  lazy val cacheMap = mvStore.openMap[URI, String]("response-bodies")
  lazy val calculatedCache = mvStore.openMap[String, SimpleWishlist]("wishlists")
  lazy val uriFetcher = MVStoreHttpCache(mvStore, cacheMap)

  case object Refresh
  case object Fetch

  val dataActor = actor("Controller")(new Act{

    var currentData: CleanWishlist.CleanOr = _

    whenStarting {
      self ! Refresh
    }

    become {
      case Refresh =>
        import scala.concurrent.ExecutionContext.Implicits.global
        val resultF = WishlistFetcher.fetchAmazonUKWishlistPages(fetchURI = uriFetcher)(identity)("1PZHU4HY3MXLI").map(CleanWishlist.apply)
        resultF pipeTo sender()
        resultF pipeTo self
      case x: CleanWishlist.CleanOr =>
        currentData = x
      case Fetch =>
        sender() ! currentData
    }

  })

  import MediaTypes._
  import concurrent.duration._
  import concurrent.ExecutionContext.Implicits.global
  import org.json4s.jackson.Serialization.write
  import org.json4s.JsonDSL._
  startServer(interface = "localhost", port = 7119) {
    path("hello") {
      get {
        complete {
          <h1>Say hello to spray</h1>
        }
      }
    } ~ path("get") {
      get {
        respondWithMediaType(`application/json`) {
          complete {
            implicit val timeout = Timeout(1.second)
            ask(dataActor, Fetch).mapTo[CleanWishlist.CleanOr].map {
              rt =>
                implicit val fmts = DefaultFormats
                write(rt.get)
            }
          }
        }
      }
    } ~ path("update") {
      get {
        respondWithMediaType(`application/json`) {
          complete {
            implicit val timeout = Timeout(20.seconds)
            ask(dataActor, Refresh).mapTo[CleanWishlist.CleanOr].map {
              rt =>
                implicit val fmts = DefaultFormats
                write(rt.get)
            }
          }
        }
      }
    } ~ pathPrefix("files") {
      encodeResponse(Gzip) {
        getFromBrowseableDirectory(scala.util.Properties.userDir)
      }
    }
  }
}
