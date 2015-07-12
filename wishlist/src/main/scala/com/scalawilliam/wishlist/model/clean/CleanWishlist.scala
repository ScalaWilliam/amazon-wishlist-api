package com.scalawilliam.wishlist.model.clean

import com.scalawilliam.wishlist.extraction.PageScraper
import com.scalawilliam.wishlist.extraction.pagefetcher.FetchedObject
import org.apache.http.client.utils.URIBuilder
import org.scalactic._
import org.scalactic.Accumulation._
import com.scalawilliam.wishlist.model.{WishlistItem, WishlistPageAttributes, Image}
import org.scalactic.ErrorMessage

case class CleanWishlist
(
  title: String,
  person: String,
  deliverTo: Option[String],
  image: Option[Image],
  uri: String,
  items: List[CleanWishlistItem]
  )

case class CleanWishlistItem
(
  id: String,
  title: String,
  link: Option[String],
  image: Image,
  reserveLink: String,
  addToCartLink: Option[String],
  priority: String,
  price: Option[String],
  comment: Option[String],
  reviewsLink: Option[String],
  wants: Int,
  has: Int
  )

object CleanWishlist {
  type CleanOr = CleanWishlist Or Every[ErrorMessage]

  def fromFetchedObjects(fetchedObjects: List[FetchedObject]): CleanWishlist Or Every[ErrorMessage] = {
    if (fetchedObjects.isEmpty) {
      Bad(One("No fetched objects found"))
    } else {
      /** If any of the items fail, etc, etc, return 'bad' with details of the URI. **/
      val pageDatas = for {
        fetchedObject <- fetchedObjects
        uri = fetchedObject.uri
        attributesOR = PageScraper.getAttributes(fetchedObject.document).badMap(reasons => reasons.map(reason => s"At $uri: $reason"))
        itemORs = PageScraper.getItems(fetchedObject.document)
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
