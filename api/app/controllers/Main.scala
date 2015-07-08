package controllers

import models.PlayWishlistManager
import play.api.mvc._

class Main @Inject()(wishlistManager: PlayWishlistManager) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def version = Action {
    Ok(appVersion)
  }

  def get = Action.async {
    Async.async {
      Ok(Async.await(wishlistManager.fetchWishlist()).get)
    }
  }

  def update = Action.async {
    Async.async {
      Ok(Async.await(wishlistManager.fetchCleanWishlist()).get)
    }
  }

}