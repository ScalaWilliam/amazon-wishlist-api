package com.scalawilliam.wishlist.model

case class WishlistPageAttributes
(
  title: String,
  person: Option[String],
  deliverTo: Option[String],
  image: Option[String],
  nextPageRelativeLink: Option[String],
  uri: Option[String] = None
)

