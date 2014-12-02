package com.scalawilliam.wishlist.extraction

import java.net.URI
import com.scalawilliam.util.Util
import org.jsoup.Jsoup
import scala.concurrent.{ExecutionContext, Future}

object PageFetcher {

  case class FetchedObject(uri: URI, body: String) {
    lazy val document = Jsoup.parse(body)
  }
  case class FetchOptions(f: URI => Future[String], maximumFetches: Int, nextUri: FetchedObject => Option[URI]) {
    def fetch(uri: URI)(implicit ec: ExecutionContext) = {
      for {result <- f(uri)} yield FetchedObject(uri, result)
    }
  }

  def fetchSequenceOfPages(fetchOptions: FetchOptions)(startingUri: URI)(implicit ec: ExecutionContext): Future[List[FetchedObject]] = {
    Util.recursiveFuture[URI, FetchedObject](
      maxTimes = fetchOptions.maximumFetches,
      f = fetchOptions.fetch,
      g = fetchOptions.nextUri
    )(startingUri)
  }

}
