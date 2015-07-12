package com.scalawilliam.wishlist.extraction.pagefetcher

import java.net.URI

import com.scalawilliam.util.FuturesUtil

import scala.async.Async
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created on 12/07/2015.
 */
case class RecursiveFetchOptions(startingURI: URI, fetchFunction: URI => Future[String], maximumFetches: Int, nextUri: FetchedObject => Option[URI]) {

  private def fetchOne(uri: URI)(implicit ec: ExecutionContext) = {
    Async.async {
      FetchedObject(
        uri = uri,
        body = Async.await(fetchFunction(uri))
      )
    }
  }

  def fetch(implicit ec: ExecutionContext): Future[List[FetchedObject]] = {
    FuturesUtil.recursiveFuture[URI, FetchedObject](
      maxTimes = maximumFetches,
      f = fetchOne,
      g = nextUri
    )(startingURI)
  }

}
