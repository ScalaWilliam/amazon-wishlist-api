package com.scalawilliam.wishlist.extract

import java.net.URI

import org.h2.mvstore.MVStore

object SharedDatabase {

  lazy val mvStore = MVStore.open("test.db")
  lazy val cacheMap = mvStore.openMap[URI, String]("response-bodies")

}
