package com.scalawilliam.wishlist.extract

import java.io.FileWriter
import java.net.URI

import com.scalawilliam.wishlist.extraction.PageScraper
import com.scalawilliam.wishlist.model.Image
import org.jsoup.Jsoup
import org.scalatest.OptionValues._
import org.scalatest.{Inside, Inspectors, Matchers, WordSpec}

class ScraperSpec
  extends WordSpec
    with Matchers
    with Inspectors
    with Inside {

  /**
    * The purpose of these tests is to ensure that a variety of resulting outputs
    * is achieved and that none of our default values are always-default because
    * of some sort of an error in the code
    */

  "Scraper" must {

    val startUri = new URI("http://www.amazon.co.uk/gp/registry/wishlist/1PZHU4HY3MXLI")
    val document = Jsoup.parse(startUri.toURL, 3000)

    val wishlistItems = PageScraper.getItems(document)
    val attributes = PageScraper.getAttributes(document)

    "WishlistAttribute should be good" in {
      attributes.isGood shouldBe true
      info(s"$attributes")
    }

    "WishlistAttribute next link should be set" in {
      attributes.get.nextPageRelativeLink.value should startWith("/")
    }

    "WishlistAttribute image should be set" in {
      attributes.get.image should not be empty
    }

    "Produce some results" in {
      wishlistItems should not be empty
    }

    "Produce all valid WishlistItems" in {
      forAll(wishlistItems) {
        _.isGood shouldBe true
      }
    }

    "Produce at least one item with a price" in {
      forAtLeast(1, wishlistItems) {
        _.get.price.isDefined shouldBe true
      }
    }

    "Produce images with proper extensions" in {
      forAll(wishlistItems) {
        item =>
          inside(item.get.image) {
            case Image(url, _, _) =>
              url should (endWith(".jpg") or endWith(".gif"))
          }
      }
    }

    "Produce at least one item without a price" in {
      forAtLeast(1, wishlistItems) {
        _.get.price.isDefined shouldBe false
      }
    }

    /** Now wishlist has a Purchased view: Add &reveal=purchased to URL **/
    "Produce at least one item with 'has' > 0" ignore {
      forAtLeast(1, wishlistItems) {
        _.get.has shouldBe >(0)
      }
    }

    "Produce at least one item with 'has' = 0" in {
      forAtLeast(1, wishlistItems) {
        _.get.has shouldBe 0
      }
    }

    "Produce at least one item with 'wants' = 1" in {
      forAtLeast(1, wishlistItems) {
        _.get.wants shouldBe 1
      }
    }

    "Produce at least one item with 'wants' > 1" in {
      forAtLeast(1, wishlistItems) {
        _.get.wants shouldBe >(1)
      }
    }

    "Produce at least one item with a reviews link" in {
      forAtLeast(1, wishlistItems) {
        _.get.reviewsLink.isDefined shouldBe true
      }
    }

    "Produce at least one item without a reviews link" in {
      forAtLeast(1, wishlistItems) {
        _.get.reviewsLink.isDefined shouldBe false
      }
    }

    "At least one comment should be set" in {
      forAtLeast(1, wishlistItems) {
        _.get.comment.isDefined shouldBe true
      }
    }

    "Get at least one price with pounds" in {
      forAtLeast(1, wishlistItems) {
        _.get.price.value startsWith "£"
      }
    }

    "At least one priority should be non-medium" in {
      forAtLeast(1, wishlistItems) {
        _.get.priority should not be "medium"
      }
    }

    "Print all the items out" ignore {
      wishlistItems foreach println
    }

  }
}