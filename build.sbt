lazy val amazonWishlistApi =
  (project in file("."))
  .aggregate(amazonWishlist, api)
  .dependsOn(amazonWishlist, api)

lazy val amazonWishlist = project in file("wishlist")

lazy val api = project.enablePlugins(PlayScala).dependsOn(amazonWishlist)

name := "amazon-wishlist-api-root"
