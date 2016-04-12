import com.scalawilliam.wishlist.clean.{CleanWishlistItem, CleanWishlist}
import com.scalawilliam.wishlist.model.Image
import play.api.http.Writeable
import play.api.libs.json.{JsValue, Json}

import scala.util.Try

/**
 * Created on 08/07/2015.
 */
package object controllers {

  implicit val httpWritesCleanWishlist = {
    implicit val writesImage = Json.writes[Image]
    implicit val writesCleanWishlistItem = Json.writes[CleanWishlistItem]
    implicit val writesCleanWishlist = Json.writes[CleanWishlist]
    implicitly[Writeable[JsValue]].map(Json.toJson(_: CleanWishlist))
  }

  type Inject = javax.inject.Inject
  type Singleton = javax.inject.Singleton

  val Async = scala.async.Async

}
