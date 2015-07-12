package com.scalawilliam.wishlist.extraction

import java.net.URI

import com.scalawilliam.wishlist.extraction.pagefetcher.{FetchedObject, RecursiveFetchOptions}
import com.scalawilliam.wishlist.manager.WishlistId

import scala.concurrent.Future

case class WishlistFetcher(wishlistId: WishlistId, fetchURI: URI => Future[String]) {

  def rootPath = new URI("http://www.amazon.co.uk")

  def entryUri = new URI(s"$rootPath/gp/registry/wishlist/${wishlistId.wishlistId}")

  def getNextUri(fetchedObject: FetchedObject): Option[URI] = {
    for {
      attributes <- PageScraper.getAttributes(fetchedObject.document).toOption
      nextPageLink <- attributes.nextPageRelativeLink
    } yield new URI(s"$rootPath$nextPageLink")
  }

  def fetchOptions = RecursiveFetchOptions(
    startingURI = entryUri,
    fetchFunction = fetchURI,
    maximumFetches = 10,
    nextUri = getNextUri _
  )

}
