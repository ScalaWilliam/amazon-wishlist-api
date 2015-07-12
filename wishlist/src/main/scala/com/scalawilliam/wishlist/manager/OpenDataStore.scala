package com.scalawilliam.wishlist.manager

import java.net.URI

import org.h2.mvstore.{MVMap, MVStore}

/**
 * Created on 12/07/2015.
 */
case class OpenDataStore(mVStore: MVStore, mVMap: MVMap[URI, String]) {
  def close(): Unit = mVStore.close()
}
