package com.scalawilliam.wishlist.clean

import java.net.URI

import com.scalawilliam.wishlist.extraction.PageScraper
import org.apache.http.client.utils.URIBuilder
import org.jsoup.nodes.Document
import org.scalactic.Accumulation._
import org.scalactic._

case class CleanWishlist
(
  title: String,
  person: Option[String],
  deliverTo: Option[String],
  image: Option[String],
  uri: String,
  items: List[CleanWishlistItem]
)

object CleanWishlist {
  type CleanOr = CleanWishlist Or Every[ErrorMessage]

  def fromFetchedObjects(fetchedObjects: List[(URI, Document)]): CleanWishlist Or Every[ErrorMessage] = {
    if (fetchedObjects.isEmpty) {
      Bad(One("No fetched objects found"))
    } else {
      /** If any of the items fail, etc, etc, return 'bad' with details of the URI. **/
      val pageDatas = for {
        (uri, document) <- fetchedObjects
        attributesOR = PageScraper.getAttributes(document).badMap(reasons => reasons.map(reason => s"At $uri: $reason"))
        itemORs = PageScraper.getItems(document)
        itemsOR = if (itemORs.exists(_.isBad)) {
          Bad(itemORs.collect { case Bad(reasons) => reasons.map(reason => s"At $uri: $reason") }.reduce(_ ++ _))
        } else {
          Good(itemORs.collect { case Good(stuff) => stuff })
        }
      } yield withGood(attributesOR, itemsOR) {
        (attributes, items) =>
          (uri, attributes, items)
      }

      if (pageDatas.exists(_.isBad)) {
        Bad(pageDatas.collect { case Bad(reasons) => reasons }.reduce(_ ++ _))
      } else {
        val yay = pageDatas.collect { case Good(stuff) => stuff }
        val startUri = yay.head._1
        val firstAttributes = yay.head._2
        val allItems = yay.flatMap(_._3)
        val items = for {
          item <- allItems
        } yield CleanWishlistItem(
          id = item.id,
          title = item.title,
          link = item.itemRelativeLink.map(lnk => new URIBuilder(startUri).setPath(lnk).build().toString),
          image = item.image,
          reserveLink = new URIBuilder(startUri).setPath(item.reserveLinkRelative).build().toString,
          priority = item.priority,
          comment = item.comment,
          price = item.price,
          addToCartLink = item.addToCartRelative.map(rel => new URIBuilder(startUri).setPath(rel).build().toString),
          reviewsLink = item.reviewsLink,
          wants = item.wants,
          has = item.has
        )

        Good(CleanWishlist(
          title = firstAttributes.title,
          person = firstAttributes.person,
          deliverTo = firstAttributes.deliverTo,
          image = firstAttributes.image,
          uri = startUri.toString,
          items = items
        )
        )
      }
    }
  }

}
