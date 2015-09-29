
libraryDependencies ++= {
  Seq (
    "org.jsoup"           % "jsoup" % "1.8.3",
    "org.apache.httpcomponents" % "httpclient" % "4.5.1",
    "joda-time"           % "joda-time" % "2.8.2",
    "org.joda"            % "joda-convert" % "1.7",
    "com.h2database"      % "h2" % "1.4.189",
    "org.scalatest"       %% "scalatest" % "2.2.5" % Test,
    "org.scalactic"       %% "scalactic" % "2.2.5",
    "io.spray"            %%  "spray-client"  % "1.3.3",
    "com.typesafe.akka"   %%  "akka-actor"    % "2.3.14",
    "com.typesafe.akka"   %%  "akka-agent"    % "2.3.14",
    "com.typesafe.akka"   %%  "akka-testkit"  % "2.3.14"   % "test",
    "org.json4s" %% "json4s-jackson" % "3.2.11",
    "org.scala-lang.modules" %% "scala-async" % "0.9.5"
  )
}
