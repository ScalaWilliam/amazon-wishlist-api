package com.scalawilliam.wishlist.model.clean

import com.scalawilliam.wishlist.model.Image

/**
  * Created by me on 12/04/2016.
  */
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
