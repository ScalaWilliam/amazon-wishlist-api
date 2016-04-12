package com.scalawilliam.wishlist.extract

import java.io.File

import akka.actor.ActorSystem
import com.scalawilliam.util.MVStoreAsyncHttpCache
import com.scalawilliam.wishlist.extraction.WishlistFetcher
import com.scalawilliam.wishlist.manager.{DataStoreOptions, WishlistId}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class WishlistFetcherSpec extends WordSpec with Matchers with ScalaFutures with BeforeAndAfterAll {

  /** http://samwrx.surge.sh/akka/2016/01/11/akkaloggingfilters.html **/
  implicit lazy val system = ActorSystem(name = "test", classLoader = Option(getClass.getClassLoader))

  lazy val uriFetcher = MVStoreAsyncHttpCache(DataStoreOptions(
    databaseName = s"target${File.separator}wf.db",
    mapName = "response-bodies"
  ).open())

  override protected def afterAll(): Unit = {
    uriFetcher.openDataStore.close()
    system.shutdown()
    super.afterAll()
  }

  "Wishlist fetcher" must {
    import scala.concurrent.ExecutionContext.Implicits.global
    val resultsF = WishlistFetcher(
      wishlistId = WishlistId.myWishlistId,
      uriFetcher.receive
    ).fetchOptions.fetch

    "Not fail" in {
      resultsF.futureValue should have size 2
    }
    "Show something" in {
      resultsF.value.get.get
    }
  }

  override implicit def patienceConfig = PatienceConfig(timeout = scaled(Span(10, Seconds)), interval = scaled(Span(1, Second)))

}
