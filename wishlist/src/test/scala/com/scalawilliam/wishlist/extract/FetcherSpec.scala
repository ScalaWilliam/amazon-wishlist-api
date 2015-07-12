package com.scalawilliam.wishlist.extract

import java.net.URI

import com.scalawilliam.wishlist.extraction.pagefetcher.RecursiveFetchOptions
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class FetcherSpec extends WordSpec with Matchers with ScalaFutures {

  "Fetcher" must {
    "Fetch multiple things as required" in {
      val stubAssembler = Map(
        new URI("uri:test:stub/first") -> "Test body 1",
        new URI("uri:test:stub/second") -> "Test body 2"
      )
      import scala.concurrent.ExecutionContext.Implicits.global
      val results = RecursiveFetchOptions(
        startingURI = new URI("uri:test:stub/first"),
        fetchFunction = (u: URI) => Future.successful(stubAssembler(u)),
        maximumFetches = 10,
        nextUri = {fo =>
        if ( fo.uri == stubAssembler.head._1 ) Option(stubAssembler.drop(1).head._1) else None}
      ).fetch.futureValue
      results should have size 2
      results.map(_.body) shouldBe List("Test body 1", "Test body 2")
    }
  }
}
