package models


import com.scalawilliam.wishlist.webapp.WishlistManager
import javax.inject.{Inject, Singleton}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.{ExecutionContext, Future, blocking}

/**
 * Created on 08/07/2015.
 */
@Singleton
class PlayWishlistManager @Inject() (applicationLifecycle: ApplicationLifecycle) extends WishlistManager {
  fetchCleanWishlist()
  override implicit def executionContext: ExecutionContext = ExecutionContext.Implicits.global
  applicationLifecycle.addStopHook(() => Future { blocking { mvStore.close() }})
}
