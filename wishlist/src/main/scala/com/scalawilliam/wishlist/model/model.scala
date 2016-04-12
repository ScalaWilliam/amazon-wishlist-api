package com.scalawilliam.wishlist.model

import java.net.URI

import org.joda.time.DateTime


case class Image
(
  // http://....jpg
  src: String,
  width: Int,
  height: Int
  )

object Image {
  def stub = Image("http://test", 20, 30)
}

case class SimpleWishlist
(
  id: String,
  fetchDatetime: DateTime,
  mainUri: URI,
  attributes: WishlistPageAttributes,
  items: List[WishlistItem]
  )


case class WishlistItem
(
  //B004BDOUAM
  id: String,
  title: String,
  // /dp/B004BDOUAI/ref=wl_it_dp_o_pC_nS_ttl?_encoding=UTF8&colid=1FY1N9FN7CLX8&coliid=ILQRGIIW6BUKA
  itemRelativeLink: Option[String],
  // Wed Jul 02 00:00:00 BST 2014
  addedOn: String,
  image: Image,
  // /gp/registry/side/reserve?ie=UTF8&id=<ID>&itemId=<ITEM-ID>&type=wishlist
  reserveLinkRelative: String,
  comment: Option[String],
  priority: String,
  price: Option[String],
  // Option(http://www.../product-reviews/<ID>)
  reviewsLink: Option[String],
  addToCartRelative: Option[String],
  wants: Int,
  has: Int
  )

object WishlistItem {
  def stub = WishlistItem(
    id = "B004BDOUAM",
    title = "test item",
    itemRelativeLink = Option("/dp/B004BDOUAI/ref=wl_it_dp_o_pC_nS_ttl?_encoding=UTF8&colid=1FY1N9FN7CLX8&coliid=ILQRGIIW6BUKA"),
    addedOn = "Wed Jul 02 00:00:00 BST 2014",
    image = Image.stub,
    reserveLinkRelative = "/gp/registry/side/reserve?ie=UTF8&id=<ID>&itemId=<ITEM-ID>&type=wishlist",
    addToCartRelative = Option("/hah"),
    comment = None,
    priority = "high",
    price = None,
    reviewsLink = None,
    wants = 1,
    has = 0
  )
}

case class WishlistPageAttributes
(
  title: String,
  person: String,
  deliverTo: Option[String],
  image: Option[String],
  nextPageRelativeLink: Option[String],
  uri: Option[String] = None
  )

object WishlistPageAttributes {
  def stub = WishlistPageAttributes(
    title = "Test page",
    person = "Johnny",
    deliverTo = Option("Northumbria"),
    image = None,
    nextPageRelativeLink = None
  )
}