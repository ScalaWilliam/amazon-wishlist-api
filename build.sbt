lazy val amazonWishlistApi =
  (project in file("."))
    .aggregate(api, wishlistScraper)
    .dependsOn(api)

lazy val wishlistScraper = (project in file("wishlist-scraper"))
  .settings(
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.9.2",
      "joda-time" % "joda-time" % "2.9.4",
      "org.joda" % "joda-convert" % "1.8.1",
      "org.scalactic" %% "scalactic" % "2.2.6",
      "org.apache.httpcomponents" % "httpclient" % "4.5.2"
    )
  )

lazy val api = project
  .enablePlugins(PlayScala)
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(wishlistScraper)
  .settings(
    libraryDependencies += ws,
    libraryDependencies += "com.typesafe.akka" %% "akka-agent" % "2.4.7",
    libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.5",
    version := "2.0",
    git.useGitDescribe := true,
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      buildInfoBuildNumber,
      git.gitHeadCommit
    ),
    mappings in SbtNativePackager.Universal in packageZipTarball += file("README.md") -> "README.md",
    publishArtifact in(Compile, packageBin) := false,
    publishArtifact in(Universal, packageZipTarball) := true,
    publishArtifact in(Compile, packageDoc) := false,
    name := "amazon-wishlist-api"
  )

name := "amazon-wishlist-api-root"
