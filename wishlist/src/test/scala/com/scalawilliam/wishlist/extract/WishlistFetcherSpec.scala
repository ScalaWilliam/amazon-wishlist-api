package com.scalawilliam.wishlist.extract

import akka.actor.ActorSystem
import com.scalawilliam.util.MVStoreAsyncHttpCache
import com.scalawilliam.wishlist.extraction.WishlistFetcher
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class WishlistFetcherSpec extends WordSpec with Matchers with ScalaFutures with BeforeAndAfterAll {

//  implicit lazy val system = ActorSystem()
//  implicit val fetcher = WishlistFetcher(
//    wishlistId =
//  )
//
//  override protected def afterAll(): Unit = {
//    system.shutdown()
//    super.afterAll()
//  }
//
//  lazy val uriFetcher = MVStoreAsyncHttpCache(SharedDatabase.mvStore, SharedDatabase.cacheMap)
//  "Wishlist fetcher" must {
//    import scala.concurrent.ExecutionContext.Implicits.global
//    val resultsF = WishlistFetcher.fetchAmazonUKWishlistPages(fetchURI = uriFetcher)(identity)("1FY1N9FN7CLX8")
//    "Not fail" in {
//      resultsF.futureValue._2 should have size 3
//    }
    "Give us a nice wishlist" in {
//      val good = WishlistFetcher.convertPagesToSimpleWishlist(resultsF.futureValue)
//      println(good.get._1)
//      good.get._2.size shouldBe <(100)
//      good.get._2.size shouldBe >(10)
//      good.get._2 foreach println
//    }
  }
//  override implicit def patienceConfig = PatienceConfig(timeout = scaled(Span(10, Seconds)), interval = scaled(Span(1, Second)))
}
