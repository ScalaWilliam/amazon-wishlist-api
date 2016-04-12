package com.scalawilliam.wishlist.extraction

import java.net.URI

import com.scalawilliam.wishlist.extraction.pagefetcher.{FetchedObject, RecursiveFetchOptions}
import org.jsoup.Jsoup

import scala.concurrent.Future

case class WishlistFetcher(wishlistId: String, fetchURI: URI => Future[String]) {

  def fetchOptions = {
    val wfp = WishlistFetcherPath(wishlistId)
    RecursiveFetchOptions(
      startingURI = wfp.entryUri,
      fetchFunction = fetchURI,
      maximumFetches = 10,
      nextUri = { f: FetchedObject => wfp.getNextUri(Jsoup.parse(f.body)) }
    )
  }

}
