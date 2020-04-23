# HTTP applications are just a Kleisli function from a streaming request to a polymorphic effect of a streaming response.
# So what's the problem?

My implementation of Http4s for learning purposes, based on [Ross Baker's presentation][presentation]
[and his slides][slides]

The code in his slides actually compiles and runs.

There was still a fair amount to do though to get everything working. Ross's approach 
is to add functionality incrementally, creating several versions.

There are nine versions in total. The final version is called `http4sFinal`

The goal is to implemented an http application as a single function, taking a request 
as input and returning a response as output.

Not a new idea. Rack in Ruby and many other frameworks take a similar approach. The
difference with http4s is that the function should be pure and side effect free.

Side effects are deferred to "the end of the world", Haskell style.
To Achieve this Cats and Cats-effects are used extensively. 

In addition to Cats the following libraries have been added as dependencies:

* [Scala test][test]
* [Cats effect][effects]
* [Fs2 streams][fs2]
* [Fs2 io][fs2.io]

The rest of this readme was generated from a Cats project template.

[presentation]:https://www.youtube.com/watch?v=urdtmx4h5LE&t=1629s
[slides]:https://rossabaker.github.io/boston-http4s/#2
[test]: http://www.scalatest.org/
[effects]:https://typelevel.org/cats-effect/
[fs2]:https://fs2.io/guide.html
[fs2.io]:https://fs2.io/io.html


# Scala with Cats Code

Sandbox project for the exercises in the book [Scala with Cats][book].
Based on the [cats-seed.g8][cats-seed] template by [Underscore][underscore].

Copyright beckfordp. Licensed [CC0 1.0][license].

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)][gitter]

## Getting Started

You will need to have Git, Java 8, and [SBT][sbt] installed.
You will also need an internet connection to run the exercises.
All other dependencies are either included with the repo
or downloaded on demand during compilation.

Start SBT using the `sbt` command to enter SBT's *interactive mode*
(`>` prompt):

```bash
$ sbt
[info] Loading global plugins from <DIRECTORY>
[info] Loading project definition from <DIRECTORY>
[info] Set current project to <PROJECT_NAME>

>
```

From the SBT prompt you can run the code in `Main.scala`:

```bash
> run
[info] Updating {file:<DIRECTORY>}cats-sandbox...
[info] Resolving jline#jline;2.14.4 ...
[info] Done updating.
[info] Compiling 1 Scala source to <DIRECTORY>...
[info] Running sandbox.Main
Hello Cats!
[success]
```

You can also start a *Scala console* (`scala>` prompt)
to play with small snippets of code:

```bash
> console
[info] Starting scala interpreter...
[info]
Welcome to Scala 2.12.3 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_112).
Type in expressions for evaluation. Or try :help.

scala> import cats._, cats.implicits._, cats.data._
import cats._
import cats.implicits._
import cats.data._

scala> "Hello " |+| "Cats!"
res0: String = Hello Cats!

scala>
```

Press `Ctrl+D` to quit the Scala console
and return to SBT interactive mode.

Press `Ctrl+D` again to quit SBT interactive mode
and return to your shell.

### Notes on Editors and IDEs

If you don't have a particular preference for a Scala editor or IDE,
we strongly recommend you do the exercises for this course using
the [Atom][atom] editor and a Linux or OS X terminal.
See the instructions below to get started.

If you want to use [Scala IDE][scala-ide] for Eclipse,
we recommend using [sbteclipse][sbteclipse].
Follow the instructions on the `sbteclipse` web page
to install it as a global SBT plugin.

If you want to use IntelliJ IDEA,
follow the instructions for [Importing an SBT Project][intellij-setup]
in the IntelliJ online documentation.

### Asking Questions

If you want to discuss the content or exercises with the authors,
join us in our chat room on [Gitter][gitter].

[cats-seed]: https://github.com/underscoreio/cats-seed.g8
[underscore]: https://underscore.io
[book]: https://underscore.io/books/advanced-scala
[license]: https://creativecommons.org/publicdomain/zero/1.0/
[sbt]: http://scala-sbt.org
[gitter]: https://gitter.im/underscoreio/scala?utm_source=essential-scala-readme&utm_medium=badge&utm_campaign=essential-scala
[atom]: https://atom.io
[scala-ide]: http://scala-ide.org
[sbteclipse]: https://github.com/typesafehub/sbteclipse
[intellij-idea]: https://www.jetbrains.com/idea
[intellij-setup]: https://www.jetbrains.com/help/idea/2016.1/getting-started-with-sbt.html#import_project
