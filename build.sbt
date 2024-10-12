scalaVersion := "2.13.15"

libraryDependencies ++=
  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  )

crossTarget := baseDirectory.value / "target" / "build"

Compile / scalaSource := baseDirectory.value / "src"
Compile / selectMainClass := Some("Main")

lazy val spro = (project in file("."))
  .settings(
    name := "spro",
    assembly / mainClass := Some("Main"),
    assembly / assemblyJarName := "spro.jar",
  )
