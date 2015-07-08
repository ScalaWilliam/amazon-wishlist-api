package com.scalawilliam.wishlist.extract

import java.io.File
import java.net.URI
import org.h2.mvstore.MVStore

object SharedDatabase {

  lazy val mvStore = MVStore.open(s"target${File.separator}test.db")
  lazy val cacheMap = mvStore.openMap[URI, String]("response-bodies")

}
