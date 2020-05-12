import sbtcrossproject.{crossProject, CrossType}

lazy val server = (project in file("server")).settings(commonSettings).settings(
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.1.2",
    guice,
		"org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
		"com.typesafe.play" %% "play-slick" % "5.0.0",
		"com.typesafe.slick" %% "slick-codegen" % "3.3.2",
		"com.typesafe.play" %% "play-json" % "2.8.1",
    "org.postgresql" % "postgresql" % "42.2.11",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
    "org.mindrot" % "jbcrypt" % "0.4",
    jdbc,
    "org.xerial" % "sqlite-jdbc" % "3.7.2",
    "net.kaliber" % "scala-mailer-play_2.11" % "6.0.0",
    "javax.mail" % "mail" % "1.4.1",
    "org.apache.commons" % "commons-compress" % "1.14",
    "commons-net" % "commons-net" % "3.6",
    specs2 % Test
  ),
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
  EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(commonSettings).settings(
  scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.5",
		"me.shadaj" %%% "slinky-core" % "0.6.3",
		"me.shadaj" %%% "slinky-web" % "0.6.3",
		"com.typesafe.play" %% "play-json" % "2.8.1"
  ),
	scalacOptions += "-P:scalajs:sjsDefinedByDefault"
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.12.10",
  organization := "edu.trinity"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
herokuAppName in Compile := "playstories"