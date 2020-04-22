name := "my-http4s"
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.13.1"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.3" withSources() withJavadoc()
libraryDependencies += "co.fs2" %% "fs2-core" % "2.2.1"
libraryDependencies += "co.fs2" %% "fs2-io" % "2.2.1"

// scalac options come from the sbt-tpolecat plugin so need to set any here

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
