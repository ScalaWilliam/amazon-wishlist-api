name := "amazon-wishlist-api"

scalaVersion := "2.11.2"

version := "1.0"

resolvers ++= Seq(
  "BaseX Maven Repository" at "http://files.basex.org/maven",
  "hypergraphdb" at "http://hypergraphdb.org/maven"
//  "XQJ Maven Repository" at "http://xqj.net/maven"
)


libraryDependencies += "org.hypergraphdb" % "hgdb" % "1.2"

libraryDependencies += "org.hypergraphdb" % "hgbdbje" % "1.2"

libraryDependencies += "org.hypergraphdb" % "hgdbmjson" % "1.2"

libraryDependencies ++= Seq (
  "org.basex" % "basex" % "7.9",
//  "net.xqj" % "basex-xqj" % "1.3.0",
//  "com.xqj2" % "xqj2" % "0.2.0",
//  "javax.xml.xquery" % "xqj-api" % "1.0",
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.apache.httpcomponents" % "httpclient" % "4.3.5",
//  "net.sf.saxon" % "Saxon-HE" % "9.5.1-5",
  "joda-time" % "joda-time" % "2.3",
  "com.typesafe.akka" %% "akka-actor" % "2.3.5",
  "org.joda" % "joda-convert" % "1.6"
//  "org.eclipse.jetty" % "jetty-server" % "9.1.3.v20140225",
//  "org.eclipse.jetty" % "jetty-webapp" % "9.1.3.v20140225",
//  "org.eclipse.jetty" % "jetty-servlet" % "9.1.3.v20140225",
//  "org.scalatra" %% "scalatra" % "2.3.0.M1",
//  "org.scalatra" %% "scalatra-json" % "2.3.0.M1"
)

libraryDependencies += "com.h2database" % "h2" % "1.4.181"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % Test

libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.1"

libraryDependencies += "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1"

libraryDependencies += "com.github.scala-incubator.io" % "scala-io-core_2.11" % "0.4.3-1"

libraryDependencies += "com.github.scala-incubator.io" % "scala-io-file_2.11" % "0.4.3-1"

libraryDependencies ++= {
  val akkaV = "2.3.5"
  val sprayV = "1.3.1"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-client"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test"
  )
}


libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.10"