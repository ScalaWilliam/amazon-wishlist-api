package com.scalawilliam.wishlist.extract

import java.io.File
import com.scalawilliam.wishlist.manager.DataStoreOptions

object SharedDatabase {

  def sharedOptions = DataStoreOptions(
    databaseName = s"target${File.separator}test.db",
    mapName = "response-bodies"
  )

}
