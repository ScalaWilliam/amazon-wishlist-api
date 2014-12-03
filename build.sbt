import com.typesafe.sbt.SbtNativePackager.packageArchetype
import NativePackagerHelper.directory

name := "amazon-wishlist-api"

scalaVersion := "2.11.4"

version := "1.0"

packageArchetype.java_application

libraryDependencies ++= {
  val akkaV = "2.3.5"
  val sprayV = "1.3.1"
  Seq (
    "org.jsoup"           % "jsoup" % "1.8.1",
    "org.apache.httpcomponents" % "httpclient" % "4.3.6",
    "joda-time"           % "joda-time" % "2.3",
    "org.joda"            % "joda-convert" % "1.6",
    "com.h2database"      % "h2" % "1.4.181",
    "org.scalatest"       %% "scalatest" % "2.2.1" % Test,
    "org.scalactic"       %% "scalactic" % "2.2.1",
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-client"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.github.scala-incubator.io" % "scala-io-core_2.11" % "0.4.3-1",
    "com.github.scala-incubator.io" % "scala-io-file_2.11" % "0.4.3-1",
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.json4s" %% "json4s-jackson" % "3.2.10"
  )
}

mainClass := Option("com.scalawilliam.wishlist.webapp.WishlistApp")

mappings in SbtNativePackager.Universal in packageZipTarball += file("README.md") -> "README.md"

mappings in SbtNativePackager.Universal ++= directory("ui")

publishArtifact in (Compile, packageBin) := false

publishArtifact in (Universal, packageZipTarball) := true

publishArtifact in (Compile, packageDoc) := false
