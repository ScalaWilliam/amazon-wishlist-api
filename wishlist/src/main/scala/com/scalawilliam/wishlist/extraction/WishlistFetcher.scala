package com.scalawilliam.wishlist.extraction

import java.net.URI
import com.scalawilliam.wishlist.extraction.PageFetcher.{FetchOptions, FetchedObject}
import com.scalawilliam.wishlist.model.{WishlistItem, WishlistPageAttributes, SimpleWishlist}
import org.joda.time.DateTime
import org.scalactic.Accumulation._
import scala.concurrent.{ExecutionContext, Future}

object WishlistFetcher {

  def fetchAmazonUKWishlistPages(fetchURI: URI => Future[String])(updateOptions: FetchOptions => FetchOptions)(wishlistId: String)(implicit ec: ExecutionContext): Future[List[FetchedObject]] = {
    val rootPath = new URI("http://www.amazon.co.uk")
    val entryUri = new URI(s"$rootPath/gp/registry/wishlist/$wishlistId")
    val fetchOptions = FetchOptions(fetchURI, maximumFetches = 10, nextUri = {fo =>
      PageScraper.getAttributes(fo.document).toOption.flatMap(_.nextPageRelativeLink).map(relative => new URI(s"$rootPath$relative"))}
    )
    for {
      items <- PageFetcher.fetchSequenceOfPages(updateOptions(fetchOptions))(entryUri)
    } yield items
  }

  import org.scalactic._
  type ResultType =  (WishlistPageAttributes, List[WishlistItem]) Or Every[ErrorMessage]
  def convertPagesToSimpleWishlist(input: (URI, List[FetchedObject])): ResultType = {
    if ( input._2.isEmpty ) {
      Bad(One("No fetched objects found"))
    } else {
      val pagesResults = for {
        item <- input._2
        attributes = PageScraper.getAttributes(item.document)
        wishlistItems = PageScraper.getItems(item.document)
      } yield {
        val wishlistItemsOR = if ( wishlistItems.exists(_.isBad) ) {
          Bad(wishlistItems.collect{case Bad(reason) => reason}.reduceLeft(_ ++ _))
        } else {
          Good(wishlistItems.map(_.get))
        }
        withGood(attributes, wishlistItemsOR) {
          case (a, wior) =>
            (a.copy(uri = Option(input._1.toString)), wior)
        }
      }

      if ( pagesResults.exists(_.isBad) ) {
        Bad(pagesResults.collect{case Bad(reason) => reason}.reduceLeft(_ ++ _))
      } else {
        Good((pagesResults.head.get._1, pagesResults.flatMap(_.toSeq).flatMap(_._2)))
      }
    }

  }


}
