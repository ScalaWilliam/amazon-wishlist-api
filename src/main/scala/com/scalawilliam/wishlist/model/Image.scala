package com.scalawilliam.wishlist.model


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