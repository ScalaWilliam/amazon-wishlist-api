import com.typesafe.sbt.SbtNativePackager.packageArchetype
import NativePackagerHelper.directory

name := "amazon-wishlist-api"

organization := "com.scalawilliam"

scalaVersion := "2.11.6"

version := "1.0"

packageArchetype.java_application

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq (
    "org.jsoup"           % "jsoup" % "1.8.2",
    "org.apache.httpcomponents" % "httpclient" % "4.4.1",
    "joda-time"           % "joda-time" % "2.7",
    "org.joda"            % "joda-convert" % "1.7",
    "com.h2database"      % "h2" % "1.4.187",
    "org.scalatest"       %% "scalatest" % "2.2.4" % Test,
    "org.scalactic"       %% "scalactic" % "2.2.4",
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-client"  % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-agent"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "com.github.scala-incubator.io" % "scala-io-core_2.11" % "0.4.3-1",
    "com.github.scala-incubator.io" % "scala-io-file_2.11" % "0.4.3-1",
    "org.json4s" %% "json4s-jackson" % "3.2.11"
  )
}


mainClass := Option("com.scalawilliam.wishlist.webapp.WishlistApp")

mappings in SbtNativePackager.Universal in packageZipTarball += file("README.md") -> "README.md"

mappings in SbtNativePackager.Universal ++= directory("ui")

publishArtifact in (Compile, packageBin) := false

publishArtifact in (Universal, packageZipTarball) := true

publishArtifact in (Compile, packageDoc) := false

Seq(com.atlassian.labs.gitstamp.GitStampPlugin.gitStampSettings: _*)
