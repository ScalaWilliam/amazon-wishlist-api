package com.scalawilliam.wishlist.extraction

import java.net.URI

import org.jsoup.nodes.Document

import scala.concurrent.Future

case class WishlistFetcherPath(wishlistId: String) {

  def rootPath = new URI("http://www.amazon.co.uk")

  def entryUri = new URI(s"$rootPath/gp/registry/wishlist/${wishlistId}")

  def getNextUri(document: Document): Option[URI] = {
    for {
      attributes <- PageScraper.getAttributes(document).toOption
      nextPageLink <- attributes.nextPageRelativeLink
    } yield new URI(s"$rootPath$nextPageLink")
  }

}
