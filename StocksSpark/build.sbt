//import AssemblyKeys._
//
//assemblySettings

name := "StocksSpark"

version := "1.0"

scalaVersion := "2.11.4"


// protocol buffer support
//seq(sbtprotobuf.ProtobufPlugin.protobufSettings: _*)

// additional libraries
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.3.1" % "provided",
  "org.apache.spark" %% "spark-sql" % "1.3.1",
  "org.apache.spark" %% "spark-hive" % "1.3.1",
  "org.apache.spark" %% "spark-streaming" % "1.3.1",
  "org.apache.spark" %% "spark-mllib" % "1.3.1",
  "org.apache.commons" % "commons-lang3" % "3.0",
  "org.eclipse.jetty"  % "jetty-client" % "8.1.14.v20131031",
//  "com.fasterxml.jackson.core" % "jackson-databind" % "2.3.3",
//  "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.3.3",
  "org.apache.geode" % "gemfire-core" % "1.0.0-incubating-SNAPSHOT" excludeAll(ExclusionRule(organization = "org.jboss.netty")),
  "io.pivotal.gemfire.spark" % "gemfire-spark-connector_2.10" % "0.5.0" excludeAll(ExclusionRule(organization = "org.apache.spark")),
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

resolvers ++= Seq(
  "Apache Software Foundation" at "http://repository.apache.org/snapshots/",
  "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
  Resolver.sonatypeRepo("public")
)