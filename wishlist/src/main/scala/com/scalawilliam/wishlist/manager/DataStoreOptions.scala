package com.scalawilliam.wishlist.manager

import java.net.URI

import org.h2.mvstore.MVStore

/**
 * Created on 12/07/2015.
 */
object DataStoreOptions {
  def basic = DataStoreOptions(
    databaseName = "main.db",
    mapName = "response-bodies"
  )
}

case class DataStoreOptions(databaseName: String, mapName: String) {
  def open() = {
    val db = MVStore.open(databaseName)
    OpenDataStore(
      mVStore = db,
      mVMap = db.openMap[URI, String](mapName)
    )
  }
}
