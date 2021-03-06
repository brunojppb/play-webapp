name := """scala-web-project"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
pipelineStages := Seq(digest)

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  evolutions,
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "org.postgresql" % "postgresql" % "42.1.3",
  "org.scalikejdbc" %% "scalikejdbc"        % "3.0.+",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "3.0.+",
  "ch.qos.logback"  %  "logback-classic"    % "1.2.+",
  "de.svenkubiak" % "jBCrypt" % "0.4.1"

)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
