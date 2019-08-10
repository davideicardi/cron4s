import sbt._
import Keys._

object CompilerPlugins {
  val macroParadise = compilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
  )
  val kindProjector = compilerPlugin(
    "org.typelevel" % "kind-projector" % "0.10.3" cross CrossVersion.binary
  )
  val betterMonadicFor = compilerPlugin(
    "com.olegpy" %% "better-monadic-for" % "0.3.1"
  )

  lazy val All = Def.settings(
    libraryDependencies ++= Seq(macroParadise, kindProjector, betterMonadicFor)
  )
}