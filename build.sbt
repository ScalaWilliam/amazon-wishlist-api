lazy val amazonWishlistApi =
  (project in file("."))
    .aggregate(amazonWishlist, api)
    .dependsOn(amazonWishlist, api)

lazy val amazonWishlist = (project in file("wishlist"))
  .settings(
    libraryDependencies ++= {
      Seq(
        "org.jsoup" % "jsoup" % "1.8.3",
        "org.apache.httpcomponents" % "httpclient" % "4.5.2",
        "joda-time" % "joda-time" % "2.9.3",
        "org.joda" % "joda-convert" % "1.8.1",
        "com.h2database" % "h2" % "1.4.191",
        "org.scalatest" %% "scalatest" % "2.2.6" % Test,
        "org.scalactic" %% "scalactic" % "2.2.6",
        "io.spray" %% "spray-client" % "1.3.3",
        "com.typesafe.akka" %% "akka-actor" % "2.4.3",
        "com.typesafe.akka" %% "akka-agent" % "2.4.3",
        "com.typesafe.akka" %% "akka-testkit" % "2.4.3" % "test",
        "org.json4s" %% "json4s-jackson" % "3.3.0",
        "org.scala-lang.modules" %% "scala-async" % "0.9.5"
      )
    }
  )

lazy val api = project
  .enablePlugins(PlayScala)
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(amazonWishlist)
  .settings(
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
