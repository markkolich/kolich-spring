# kolich-spring

This Java library is a common set of classes that I frequently use to build complex <a href="http://www.springsource.org/">Spring 3</a> web-applications.

It is built around Spring 3.1.2, and does not currently support older versions of Spring.

## Latest Version

The latest stable version of this library is <a href="http://markkolich.github.com/repo/com/kolich/kolich-spring/0.0.3.1">0.0.3.1</a>.

## Resolvers

If you wish to use this artifact, you can easily add it to your existing Maven or SBT project using <a href="https://github.com/markkolich/markkolich.github.com#marks-maven2-repository">my GitHub hosted Maven2 repository</a>.

### SBT

```scala
resolvers += "Kolich repo" at "http://markkolich.github.com/repo"

val kolichSpring = "com.kolich" % "kolich-spring" % "0.0.3.1" % "compile"
```

### Maven

```xml
<repository>
  <id>Kolichrepo</id>
  <name>Kolich repo</name>
  <url>http://markkolich.github.com/repo/</url>
  <layout>default</layout>
</repository>

<dependency>
  <groupId>com.kolich</groupId>
  <artifactId>kolich-spring</artifactId>
  <version>0.0.3.1</version>
  <scope>compile</scope>
</dependency>
```

## Building

This Java library and its dependencies are built and managed using <a href="https://github.com/harrah/xsbt">SBT (the Simple Build Tool)</a> **0.12.1**.

To clone and build kolich-spring, you must have <a href="http://www.scala-sbt.org/release/docs/Getting-Started/Setup">SBT 0.12.1 installed and configured on your computer</a>.

The kolich-spring SBT <a href="https://github.com/markkolich/kolich-spring/blob/master/project/Build.scala">Build.scala</a> file is highly customized to build and package this Java artifact.  It's written to manage all dependencies and versioning.

To build, clone the repository.

    #~> git clone git://github.com/markkolich/kolich-spring.git

Run SBT from within kolich-spring.

    #~> cd kolich-spring
    #~/kolich-spring> sbt
    ...
    kolich-spring:0.0.3.1>

You will see a `kolich-spring` SBT prompt once all dependencies are resolved and the project is loaded.

In SBT, run `package` to compile and package the JAR.

    kolich-spring:0.0.3.1> package
    [info] Compiling 17 Java sources to ~/kolich-spring/target/classes...
    [info] Packaging ~/kolich-spring/dist/kolich-spring-0.0.3.1.jar ...
    [info] Done packaging.
    [success] Total time: 4 s, completed

Note the resulting JAR is placed into the **kolich-spring/dist** directory.

To create an Eclipse Java project for kolich-spring, run `eclipse` in SBT.

    kolich-spring:0.0.3.1> eclipse
    ...
    [info] Successfully created Eclipse project files for project(s):
    [info] kolich-spring

You'll now have a real Eclipse **.project** file worthy of an Eclipse import.

Note your new **.classpath** file as well -- all source JAR's are fetched and injected into the Eclipse project automatically.

## Dependencies

Naturally, this artifact depends on spring-web 3.1.2.RELEASE, and spring-webmvc 3.1.2.RELEASE.

It also firmly depends on my common package of utility classes, <a href="https://github.com/markkolich/kolich-common">kolich-common</a>.

## Licensing

Copyright (c) 2012 <a href="http://mark.koli.ch">Mark S. Kolich</a>

All code in this artifact is freely available for use and redistribution under the <a href="http://opensource.org/comment/991">MIT License</a>.

See <a href="https://github.com/markkolich/kolich-spring/blob/master/LICENSE">LICENSE</a> for details.
