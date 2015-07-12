package com.scalawilliam.util

import java.net.URI
import akka.actor.ActorSystem
import com.scalawilliam.wishlist.manager.OpenDataStore
import org.h2.mvstore.{MVMap, MVStore}
import spray.client.pipelining._
import spray.http.{HttpResponse, HttpRequest}
import scala.concurrent.{ExecutionContext, Future}
import scala.async.Async

case class MVStoreAsyncHttpCache(openDataStore: OpenDataStore) {

  def receive(uri: URI)(implicit executionContext: ExecutionContext, system: ActorSystem): Future[String] = {

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    if ( openDataStore.mVMap.containsKey(uri) ) {
      Future.successful(openDataStore.mVMap.get(uri))
    }
    else {
      Async.async {
        val body = Async.await(pipeline(Get(uri.toString))).entity.asString
        openDataStore.mVMap.put(uri, body)
        openDataStore.mVStore.commit()
        body
      }
    }
  }
  
}
