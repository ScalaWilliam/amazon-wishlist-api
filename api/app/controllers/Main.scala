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
    Ok(appVersion)
  }

  def get = Action.async {
    Async.async {
      Ok(Async.await(wishlistManager.wishlistManager.fetchWishlist).get)
    }
  }

  def update = Action.async {
    Async.async {
      Ok(Async.await(wishlistManager.wishlistManager.fetchCleanWishlist).get)
    }
  }

}