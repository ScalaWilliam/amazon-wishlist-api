import com.scalawilliam.wishlist.model.Image
import com.scalawilliam.wishlist.model.clean.{CleanWishlist, CleanWishlistItem}
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

  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  val Async = scala.async.Async

  @deprecated("replace with sbt-git lookup", "")
  private[controllers] def appVersion = {
    Try {
      Option(new java.util.jar.Manifest(getClass.getClassLoader.getResourceAsStream("META-INF/MANIFEST.MF")).getMainAttributes.getValue("Git-Head-Rev"))
    }.toOption.flatten.getOrElse("")
  }


}
