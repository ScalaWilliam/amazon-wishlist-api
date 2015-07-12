package com.scalawilliam.wishlist.extraction.pagefetcher

import java.net.URI

import com.scalawilliam.util.FuturesUtil
import org.jsoup.Jsoup

import scala.async.Async
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created on 12/07/2015.
 */
case class FetchedObject(uri: URI, body: String) {
  def document = Jsoup.parse(body)
}


