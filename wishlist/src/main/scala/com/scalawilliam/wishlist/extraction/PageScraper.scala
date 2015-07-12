package com.scalawilliam.wishlist.extraction

import com.scalawilliam.wishlist.model.{WishlistPageAttributes, Image, WishlistItem}
import org.jsoup.nodes.{Element, Document}
import scala.util.Try

// One very lovely thing about using Scalactic validation is that
// You will find out if there has been more than one problem that
// Caused a failure. Will require fewer iterations to fix problems
import com.scalawilliam.util.RichSoup._
import org.scalactic._
import org.scalactic.Accumulation._

import scala.collection.JavaConverters._

object PageScraper {

  val dateFormat = new java.text.SimpleDateFormat("d MMMMM, yyyy")
  val localIdMatch = """^item_(.*)$""".r
  val globalIdMatch = """^/dp/([A-Z0-9]+).*""".r
  val itemAdded = """Added (.*)""".r

  def extractItem(container: Element, localId: String): WishlistItem Or Every[ErrorMessage] = {
    val itemTitleOR = container.select(s"a#itemName_$localId[title][href]").optionalAttr("title") orElse {
      container.select(s"span#itemName_$localId").optionalText.map(_.trim)
    } match {
      case Some(title) => Good(title)
      case None => Bad(One(s"Could not find title for $localId"))
    }
    val itemRelativeLinkO = container.select(s"a#itemName_$localId[title][href]").optionalAttr("href")
    val addedOnOR = container.select(s"#itemAction_$localId span.a-size-small").optionalText.map(_.trim) match {
      case Some(itemAdded(when)) => Good(dateFormat.parse(when))
      case other => Bad(One(s"Could not find a valid 'added on' date. Found: $other"))
    }
    val reserveLinkRelativeOR = container.select(s"a#lnkReserve_$localId").optionalAttr("href") match {
      case Some(link) => Good(link)
      case other => Bad(One(s"Could not find a valid reserve link. Found: $other"))
    }
    val imageOR = container.select(s"#itemImage_$localId img[width][height][src]").asScala.headOption match {
      case Some(img) => Good(Image(img.attr("src"), img.attr("width").toInt, img.attr("height").toInt))
      case _ => Bad(One(s"Could not find an image"))
    }
    val wantsOR = container.select(s"#itemRequested_$localId").optionalText match {
      case Some(number) if Try(number.toInt).isSuccess => Good(number.toInt)
      case Some(other) => Bad(One(s"Could not calculate number of wanted items, got $other"))
      case None => Good(1)
    }
    val hasOR = container.select(s"#itemPurchased_$localId").optionalText match {
      case Some(number) if Try(number.toInt).isSuccess => Good(number.toInt)
      case Some(other) => Bad(One(s"Could not calculate number of items had, got $other"))
      case None => Good(0)
    }
    val commentO = container.select(s"span#itemComment_$localId").optionalText
    val priorityOR = container.select(s"span#itemPriorityLabel_$localId").optionalText.map(_.toLowerCase) match {
      case Some(priority) if Seq("highest", "high", "medium", "low", "lowest") contains priority =>
        Good(priority)
      case Some(other) =>
        Bad(One(s"Given priority not valid: $other "))
      case None =>
        Good("medium")
    }
    val priceOOR = container.select(s"div.price-section .a-color-price").optionalText.filterNot(_ == "Unavailable") match {
      case Some(priceValue) if priceValue.length > 0 && priceValue.head.toInt == 65533 =>
        // this is a pound value, but it seems that's not always picked up
        Good(Option("£" + priceValue.tail))
      case Some(priceValue) => Good(Option(priceValue))
      case _ => Good(None)
    }
    val reviewsLinkO = container.select(s"a.a-link-normal").asScala.collectFirst {
      case a if a.attr("href") contains "product-reviews" => a.attr("href")
    }
    val addToCartRelativeO = container.select(s"a#itemAddToCart_$localId[href]").optionalAttr("href")
    withGood(itemTitleOR, addedOnOR, reserveLinkRelativeOR, imageOR, priceOOR, wantsOR, hasOR, priorityOR) {
      (itemTitle, addedOn, reserveLinkRelative, image, priceO, wants, has, priority) =>
        WishlistItem(
          id = localId,
          title = itemTitle,
          itemRelativeLink = itemRelativeLinkO,
          addedOn = addedOn.toString,
          image = image,
          price = priceO,
          reviewsLink = reviewsLinkO,
          priority = priority,
          comment = commentO,
          reserveLinkRelative = reserveLinkRelative,
          wants = wants,
          has = has,
        addToCartRelative = addToCartRelativeO
        )
    }
  }

  def getAttributes(doc: Document): WishlistPageAttributes Or Every[ErrorMessage] = {

    val wishlistTitleOR = doc.select(".profile.top .profile-layout-aid-top .clip-text").optionalText match {
      case Some(text) => Good(text)
      case other => Bad(One("Could not find a title for the wishlist"))
    }

    val wishlistPersonOR = doc.select(".profile.top .g-profile-stable span:nth-child(1) .a-color-base").optionalText match {
      case Some(text) => Good(text)
      case other => Bad(One("Could not find the person for the wishlist"))
    }

    val wishlistDeliverO = doc.select(".profile.top .g-profile-stable span:nth-child(4) .a-color-base").optionalText

    val wishlistImageO = {
      doc.select(".profile.top img[src][height][width]").asScala.headOption.map { image =>
        Image(image.attr("src"), image.attr("width").toInt, image.attr("height").toInt)
      }
    }

    val nextLinkO = doc.select("#wishlistPagination .a-last a[href]").optionalAttr("href")

    withGood(wishlistTitleOR, wishlistPersonOR) {
      (title, person) =>
        WishlistPageAttributes(title, person, wishlistDeliverO, wishlistImageO, nextLinkO)
    }
  }

  def getItems(doc: Document): List[WishlistItem Or Every[ErrorMessage]] = {

    val wishlistItems = for {
      container <- doc.select("div.g-items-section > div").asScala.toIterator
      localIdMatch(localId) <- container.optionalAttr("id")
    } yield extractItem(container, localId)

    wishlistItems.toList

  }
}
