/**
 * Copyright (c) 2012 Mark S. Kolich
 * http://mark.koli.ch
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import sbt._
import sbt.Keys._

import com.typesafe.sbteclipse.plugin.EclipsePlugin._

object Dependencies {

  // Internal dependencies

  private val kolichCommon = "com.kolich" % "kolich-common" % "0.0.3" % "compile"

  // External dependencies

  private val slf4j = "org.slf4j" % "slf4j-api" % "1.7.2" % "compile"

  private val jspApi = "javax.servlet.jsp" % "jsp-api" % "2.2" % "provided"
  private val jstl = "jstl" % "jstl" % "1.2" % "provided"
  private val servlet = "org.glassfish" % "javax.servlet" % "3.0" % "provided"
  private val javaEEWebApi = "javax" % "javaee-web-api" % "6.0" % "provided" 

  private val springWeb = "org.springframework" % "spring-web" % "3.1.2.RELEASE" % "compile"
  private val springMvc = "org.springframework" % "spring-webmvc" % "3.1.2.RELEASE" % "compile"

  private val commonsIo = "commons-io" % "commons-io" % "2.4" % "compile"

  val deps = Seq(
    // Internal.
    kolichCommon,
    // External.
    slf4j,
    jspApi, jstl, servlet, javaEEWebApi,
    springWeb, springMvc,
    commonsIo)

}

object Resolvers {

  private val kolichRepo = "Kolich repo" at "http://markkolich.github.com/repo"

  val depResolvers = Seq(kolichRepo)

}

object Spring extends Build {

  import Dependencies._
  import Resolvers._

  /*
  import scalaz.Scalaz._
  import scala.xml._
  import scala.xml.transform.{ RewriteRule, RuleTransformer }
  import scala.xml.{ Attribute, Elem, MetaData, Node, Null, Text }
  import com.typesafe.sbteclipse.core.Validation
  */

  private val aName = "kolich-spring"
  private val aVer = "0.0.3.1"
  private val aOrg = "com.kolich"

  lazy val spring: Project = Project(
    aName,
    new File("."),
    settings = Defaults.defaultSettings ++ Seq(resolvers := depResolvers) ++ Seq(
      version := aVer,
      organization := aOrg,
      scalaVersion := "2.9.2",
      javacOptions ++= Seq("-Xlint"),
      shellPrompt := { (state: State) => { "%s:%s> ".format(aName, aVer) } },
      // True to export the packaged JAR instead of just the compiled .class files.
      exportJars := true,
      // Disable using the Scala version in output paths and artifacts.
      // When running 'publish' or 'publish-local' SBT would append a
      // _<scala-version> postfix on artifacts. This turns that postfix off.
      crossPaths := false,
      // Keep the scala-lang library out of the generated POM's for this artifact. 
      autoScalaLibrary := false,
      // Only add src/main/java and src/test/java as source folders in the project.
      // Not a "Scala" project at this time.
      unmanagedSourceDirectories in Compile <<= baseDirectory(new File(_, "src/main/java"))(Seq(_)),
      unmanagedSourceDirectories in Test <<= baseDirectory(new File(_, "src/test/java"))(Seq(_)),
      // Tell SBT to include our .java files when packaging up the source JAR.
      unmanagedSourceDirectories in Compile in packageSrc <<= baseDirectory(new File(_, "src/main/java"))(Seq(_)),
      // Override the SBT default "target" directory for compiled classes.
      classDirectory in Compile <<= baseDirectory(new File(_, "target/classes")),
      // Tweaks the name of the resulting JAR on a "publish" or "publish-local".
      artifact in packageBin in Compile <<= (artifact in packageBin in Compile, version) apply ((artifact, ver) => {
        val newName = artifact.name + "-" + ver
        Artifact(newName, artifact.`type`, artifact.extension, artifact.classifier, artifact.configurations, artifact.url)
      }),
      // Tweaks the name of the resulting source JAR on a "publish" or "publish-local".
      artifact in packageSrc in Compile <<= (artifact in packageSrc in Compile, version) apply ((artifact, ver) => {
        val newName = artifact.name + "-" + ver
        Artifact(newName, artifact.`type`, artifact.extension, artifact.classifier, artifact.configurations, artifact.url)
      }),
      // Tweaks the name of the resulting POM on a "publish" or "publish-local".
      artifact in makePom <<= (artifact in makePom, version) apply ((artifact, ver) => {
        val newName = artifact.name + "-" + ver
        Artifact(newName, artifact.`type`, artifact.extension, artifact.classifier, artifact.configurations, artifact.url)
      }),
      // Do not bother trying to publish artifact docs (scaladoc, javadoc). Meh.
      publishArtifact in packageDoc := false,
      // Override the global name of the artifact.
      artifactName <<= (name in (Compile, packageBin)) { projectName =>
        (config: String, module: ModuleID, artifact: Artifact) =>
          var newName = projectName
          if (module.revision.nonEmpty) {
            newName += "-" + module.revision
          }
          newName + "." + artifact.extension
      },
      /*
      // Not used; just here to document how you can override the path, name
      // and location of the packaged JAR file.
      artifactPath in Compile in packageBin <<= baseDirectory {
        base => base / "dist" / "scala-app.jar"
      },
      */
      // Override the default 'package' path used by SBT. Places the resulting
      // JAR into a more meaningful location.
      artifactPath in (Compile, packageBin) ~= { defaultPath =>
        file("dist") / defaultPath.getName
      },
      libraryDependencies ++= deps,
      retrieveManaged := true) ++
      Seq(EclipseKeys.createSrc := EclipseCreateSrc.Default,
        // Make sure SBT also fetches/loads the "src" (source) JAR's for
        // all declared dependencies.
        EclipseKeys.withSource := true,
        // This is a Java project, only.
        EclipseKeys.projectFlavor := EclipseProjectFlavor.Java/*,
        EclipseKeys.projectTransformerFactories := Seq(new EclipseTransformerFactory[RewriteRule] {
          override def createTransformer(ref: ProjectRef, state: State): Validation[RewriteRule] = {
            val rule = new RewriteRule {
              override def transform(node: Node): Seq[Node] = node match {
                case elem if (elem.label == "natures") =>
                  val newChild = elem.child ++ Elem(elem.prefix, "nature", Null, elem.scope, Text("com.sysdeo.eclipse.tomcat.tomcatnature"))
                  Elem(elem.prefix, "natures", elem.attributes, elem.scope, newChild: _*)
                case other =>
                  other
              }
            }
            rule.success
          }
        })*/))

}
