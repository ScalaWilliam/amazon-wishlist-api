package com.scalawilliam.util

import java.net.URI
import akka.actor.ActorSystem
import org.h2.mvstore.{MVMap, MVStore}
import spray.client.pipelining._
import spray.http.{HttpResponse, HttpRequest}
import scala.concurrent.{ExecutionContext, Future}

object MVStoreHttpCache {

  implicit lazy val system = ActorSystem()

  def apply(mvStore: MVStore, cacheMap: MVMap[URI, String]): URI => Future[String] = {
    import ExecutionContext.Implicits.global
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    (uri: URI) => {
      if ( cacheMap.containsKey(uri) ) {
        Future.successful(cacheMap.get(uri))
      } else {
        for {
          r <- pipeline(Get(uri.toString))
          body = r.entity.asString
        } yield {
          cacheMap.put(uri, body)
          mvStore.commit()
          body
        }
      }
    }
  }
  
}
