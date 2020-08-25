name := "test-redis"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "net.debasishg" %% "redisclient" % "3.30",
  "org.scalactic" %% "scalactic" % "3.2.0",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.13.3",
  "org.apache.logging.log4j" % "log4j-api" % "2.13.3",
  "org.apache.logging.log4j" % "log4j-core" % "2.13.3",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.2",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.2",
  "org.scalatest" %% "scalatest" % "3.2.0" % "test"
)
