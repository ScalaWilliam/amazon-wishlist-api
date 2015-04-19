package com.scalawilliam.wishlist.webapp

import java.net.URI

import akka.actor.ActorSystem
import akka.agent.Agent
import com.scalawilliam.util.MVStoreHttpCache
import com.scalawilliam.wishlist.extraction.WishlistFetcher
import com.scalawilliam.wishlist.model.clean.CleanWishlist
import org.h2.mvstore.MVStore
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write
import spray.http.MediaTypes._
import spray.routing.SimpleRoutingApp
import spray.routing.directives.OnSuccessFutureMagnet

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure, Try}

trait WishlistAppConfig {
  val httpHost = System.getProperty("http.host", "localhost")
  // "" -> /get, /hello/john -> /hello/john/get
  val contextPath = System.getProperty("http.context", "")
  val httpPort = System.getProperty("http.port", "7119").toInt
}
trait WishlistManager {
  implicit def executionContext: ExecutionContext
  lazy val mvStore = MVStore.open("main.db")
  lazy val cacheMap = mvStore.openMap[URI, String]("response-bodies")
  lazy val uriFetcher = MVStoreHttpCache(mvStore, cacheMap)
  lazy val agt = Agent(Option.empty[String])
  implicit val jFormats = DefaultFormats
  def fetchCleanWishlist(): Future[Option[String]] = {
    WishlistFetcher.fetchAmazonUKWishlistPages(fetchURI = uriFetcher)(identity)("1PZHU4HY3MXLI").map(CleanWishlist.apply).map { item =>
      item.toOption.map(write(_)).map { wlString =>
        agt.send(Option(wlString))
        wlString
      }
    }
  }
}
trait ProvidesVersion {
  def appVersion = {
    Try {
      Option(new java.util.jar.Manifest(getClass.getClassLoader.getResourceAsStream("META-INF/MANIFEST.MF")).getMainAttributes.getValue("Git-Head-Rev"))
    }.toOption.flatten.getOrElse("")
  }
}
object WishlistApp
  extends App
  with SimpleRoutingApp
  with WishlistAppConfig
  with ProvidesVersion
  with WishlistManager {

  implicit lazy val system = ActorSystem()
  implicit override def executionContext: ExecutionContext = system.dispatcher

  fetchCleanWishlist() onComplete {
    case Success(stuff) =>
      println("First load was successful.")
    case Failure(e) =>
      println(s"""First load was unsuccessful. Reason: $e""")
  }

  println(s"""Open the page at: http://$httpHost:$httpPort$contextPath""")
  startServer(interface = httpHost, port = httpPort) {
    rawPathPrefix(if (contextPath equals "") "" else separateOnSlashes(contextPath)) {
      path("version" / PathEnd) { get { complete { appVersion } } } ~
      path("get") {
      get { respondWithMediaType(`application/json`) { complete {
        agt.get()
      } } }
    } ~ path("update") {
      get { respondWithMediaType(`application/json`) {
        onSuccess(OnSuccessFutureMagnet(fetchCleanWishlist())) { stuff => complete(stuff) }
      } }
    } ~
    path("") {
      getFromFile(scala.util.Properties.userDir + java.io.File.separator + "ui/index.html")
    } ~
    getFromDirectory(scala.util.Properties.userDir + java.io.File.separator + "ui")
  }
  }
}
