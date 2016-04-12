package controllers

import akka.actor.ActorSystem
import models.PlayWishlistManager
import play.api.mvc._

import scala.concurrent.ExecutionContext

class Main @Inject()(wishlistManager: PlayWishlistManager)
                    (implicit ec: ExecutionContext, actorSystem: ActorSystem) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def version = Action {
    Ok(buildinfo.BuildInfo.gitHeadCommit.getOrElse(""))
  }

  def get = Action.async {
    Async.async {
      Ok(Async.await(wishlistManager.wishlist))
    }
  }

}