import sbt._
import Keys._

object Scalanvas extends Build {
  lazy val bananaUtil: Project = Project(
    id = "banana-util",
    base = file("banana"),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(
        "org.apache.jena" % "jena-arq" % "2.11.1",
        "org.scalaz" %% "scalaz-core" % "7.0.6",
        "com.github.jsonld-java" % "jsonld-java-jena" % "0.2"
      )
    )
  ).dependsOn(
    ProjectRef(uri("git://github.com/w3c/banana-rdf.git#95069024cf0184172c6dba9fc0be55efbeb5b863"), "banana-rdf"),
    ProjectRef(uri("git://github.com/w3c/banana-rdf.git#95069024cf0184172c6dba9fc0be55efbeb5b863"), "banana-jena")
  )

  lazy val core: Project = Project(
    id = "scalanvas-core",
    base = file("core"),
    dependencies = Seq(bananaUtil),
    settings = commonSettings
  )

  lazy val root: Project = Project(
    id = "scalanvas",
    base = file("."),
    settings = commonSettings
  ).aggregate(core)

  def commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "edu.umd.mith",
    version := "0.0.0-SNAPSHOT",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    scalaVersion := "2.10.4",
    scalacOptions := Seq(
      "-feature",
      "-language:implicitConversions",
      "-deprecation",
      "-unchecked"
    ),
    libraryDependencies <++= scalaVersion(sv => Seq(
      "com.typesafe" % "config" % "1.2.0",
      "org.slf4j" % "slf4j-simple" % "1.6.4",
      "io.argonaut" %% "argonaut" % "6.0",
      "no.arktekk" % "anti-xml_2.10" % "0.5.1"
    ))
  )
}

