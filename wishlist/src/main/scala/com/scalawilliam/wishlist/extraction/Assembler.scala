package com.scalawilliam.wishlist.extraction

import java.net.URI

import com.scalawilliam.util.Util
import com.scalawilliam.wishlist.model.{WishlistItem, WishlistPageAttributes}
import org.jsoup.Jsoup

import scala.concurrent.{ExecutionContext, Future}

object Assembler {

  case class WishlistPageAssembly(uri: URI, attributes: WishlistPageAttributes, items: List[WishlistItem])
}
