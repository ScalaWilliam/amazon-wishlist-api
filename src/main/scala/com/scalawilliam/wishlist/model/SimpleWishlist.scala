package com.scalawilliam.wishlist.model

import java.net.URI
import org.joda.time.DateTime

case class SimpleWishlist(
id: String,
fetchDatetime: DateTime,
mainUri: URI,
attributes: WishlistPageAttributes,
items: List[WishlistItem]
)
