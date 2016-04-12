lazy val amazonWishlistApi =
  (project in file("."))
    .aggregate(amazonWishlist, api, htmlExtractor)
    .dependsOn(api)

lazy val htmlExtractor = (project in file("html-extractor"))
  .settings(
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.8.3",
      "joda-time" % "joda-time" % "2.9.3",
      "org.joda" % "joda-convert" % "1.8.1",
      "org.scalactic" %% "scalactic" % "2.2.6"
    )
  )

lazy val amazonWishlist = (project in file("wishlist"))
  .settings(
    libraryDependencies ++=
      Seq(
        "org.apache.httpcomponents" % "httpclient" % "4.5.2",
        "com.typesafe.akka" %% "akka-actor" % "2.4.3",
        "com.typesafe.akka" %% "akka-agent" % "2.4.3",
        "org.scala-lang.modules" %% "scala-async" % "0.9.5"
      )
  )
  .dependsOn(htmlExtractor)

lazy val api = project
  .enablePlugins(PlayScala)
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(amazonWishlist)
  .settings(
    libraryDependencies += ws,
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
