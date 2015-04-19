package com.scalawilliam.wishlist.webapp

import java.net.URI

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.http.Http
import akka.http.model.{ContentTypes, HttpEntity}
import akka.http.server.Directives._
import akka.stream.ActorFlowMaterializer
import com.scalawilliam.util.MVStoreHttpCache
import com.scalawilliam.wishlist.extraction.WishlistFetcher
import com.scalawilliam.wishlist.model.clean.CleanWishlist
import org.h2.mvstore.MVStore
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

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

trait Service
  extends ProvidesVersion
  with WishlistManager
  with WishlistAppConfig {

  implicit def executionContext: ExecutionContext

  def version = {
    Try {
      Option(new java.util.jar.Manifest(getClass.getClassLoader.getResourceAsStream("META-INF/MANIFEST.MF")).getMainAttributes.getValue("Git-Head-Rev"))
    }.toOption.flatten.getOrElse("undefined")
  }

  val routes = {
    rawPathPrefix(if (contextPath equals "") "" else separateOnSlashes(contextPath)) {
      path("version" / PathEnd) {
        get {
          complete {
            appVersion
          }
        }
      } ~
        path("get") {
          get {
            complete {
              agt.get().map { json => HttpEntity(
                contentType = ContentTypes.`application/json`,
                string = json
              )
              }
            }
          }
        } ~ path("update") {
        get {
          onSuccess(fetchCleanWishlist()) { resp => complete {
            resp.map { json =>
              HttpEntity(
                contentType = ContentTypes.`application/json`,
                string = json
              )
            }
          }
          }
        }
      } ~
        path("") {
          getFromFile(scala.util.Properties.userDir + java.io.File.separator + "ui/index.html")
        } ~
        getFromDirectory(scala.util.Properties.userDir + java.io.File.separator + "ui")
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
  with Service {

  fetchCleanWishlist() onComplete {
    case Success(stuff) =>
      println("First load was successful.")
    case Failure(e) =>
      println( s"""First load was unsuccessful. Reason: $e""")
  }

  println( s"""Open the page at: http://$httpHost:$httpPort$contextPath""")

  implicit lazy val system = ActorSystem()
  override implicit def executionContext: ExecutionContext = system.dispatcher
  implicit lazy val materializer = ActorFlowMaterializer()

  Http().bindAndHandle(routes, httpHost, httpPort)
}