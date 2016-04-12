package com.scalawilliam.wishlist.model

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