package com.scalawilliam.wishlist.extract

import java.io.File
import java.net.URI
import com.scalawilliam.wishlist.extraction.PageFetcher
import com.scalawilliam.wishlist.extraction.PageFetcher.FetchOptions
import org.scalatest._
import org.scalatest.concurrent.{ScalaFutures, Futures}
import scala.concurrent.Future

class FetcherSpec extends WordSpec with Matchers with ScalaFutures {

  "Fetcher" must {
    import scalax.io.JavaConverters._
    "Fetch multiple things as required" in {
      val stubAssembler = Map(
        new URI("uri:test:stub/first") -> "Test body 1",
        new URI("uri:test:stub/second") -> "Test body 2"
      )
      val fetchOptions = FetchOptions(u => Future.successful(stubAssembler(u)), maximumFetches = 10, nextUri = {fo =>
        if ( fo.uri == stubAssembler.head._1 ) Option(stubAssembler.drop(1).head._1) else None}
      )
      import scala.concurrent.ExecutionContext.Implicits.global
      val results = PageFetcher.fetchSequenceOfPages(fetchOptions)(new URI("uri:test:stub/first")).futureValue
      results should have size 2
      results(0).body should include ("Test body 1")
      results(1).body should include ("Test body 2")
    }
  }
}
