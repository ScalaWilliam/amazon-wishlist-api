package com.scalawilliam.wishlist.model


case class WishlistItem
(
  //B004BDOUAM
  id: String,
  title: String,
  // /dp/B004BDOUAI/ref=wl_it_dp_o_pC_nS_ttl?_encoding=UTF8&colid=1FY1N9FN7CLX8&coliid=ILQRGIIW6BUKA
  itemRelativeLink: String,
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
    itemRelativeLink = "/dp/B004BDOUAI/ref=wl_it_dp_o_pC_nS_ttl?_encoding=UTF8&colid=1FY1N9FN7CLX8&coliid=ILQRGIIW6BUKA",
    addedOn = "Wed Jul 02 00:00:00 BST 2014",
    image = Image.stub,
    reserveLinkRelative ="/gp/registry/side/reserve?ie=UTF8&id=<ID>&itemId=<ITEM-ID>&type=wishlist",
    addToCartRelative=Option("/hah"),
    comment = None,
    priority = "high",
    price = None,
    reviewsLink = None,
    wants = 1,
    has = 0
  )
}