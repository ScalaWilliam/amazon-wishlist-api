package com.scalawilliam.wishlist.model

/**
  * Created by me on 12/04/2016.
  */

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
