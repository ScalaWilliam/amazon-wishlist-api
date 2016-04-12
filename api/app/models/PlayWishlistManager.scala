package models


import akka.actor.ActorSystem
import akka.agent.Agent
import com.scalawilliam.util.MVStoreAsyncHttpCache
import com.scalawilliam.wishlist.manager.{DataStoreOptions, WishlistId, WishlistManager}
import javax.inject.{Inject, Singleton}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.{ExecutionContext, Future, blocking}

/**
  * Created on 08/07/2015.
  */
@Singleton
class PlayWishlistManager @Inject()
(applicationLifecycle: ApplicationLifecycle)
(implicit executionContext: ExecutionContext, actorSystem: ActorSystem) {
  val wishlistManager = WishlistManager(
    wishlistId = WishlistId.myWishlistId,
    httpCache = MVStoreAsyncHttpCache(openDataStore = DataStoreOptions.basic.open()),
    agt = Agent(Option.empty)
  )
  wishlistManager.fetchCleanWishlist
  applicationLifecycle.addStopHook(() => Future {
    blocking {
      wishlistManager.httpCache.openDataStore.close()
    }
  })
}
