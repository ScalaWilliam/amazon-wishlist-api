package com.scalawilliam.wishlist.model

import java.net.URI

import org.joda.time.DateTime

/**
  * Created by me on 12/04/2016.
  */
case class SimpleWishlist
(
  id: String,
  fetchDatetime: DateTime,
  mainUri: URI,
  attributes: WishlistPageAttributes,
  items: List[WishlistItem]
  )
