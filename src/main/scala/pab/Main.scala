package pab

import java.nio.file.Paths

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import fs2.{Stream, io, text}


object Main extends App {
  // println("Hello " |+| "Cats!")

  Converter.run(Nil)
}



object Converter extends IOApp {

  val converter: Stream[IO, Unit] = Stream.resource(Blocker[IO]).flatMap { blocker =>
    def fahrenheitToCelsius(f: Double): Double =
      (f - 32.0) * (5.0/9.0)

    io.file.readAll[IO](Paths.get("fahrenheit.txt"), blocker, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)
      .intersperse("\n")
      .through(text.utf8Encode)
      .through(io.file.writeAll(Paths.get("celsius.txt"), blocker))
  }

  def run(args: List[String]): IO[ExitCode] =
    converter.compile.drain.as(ExitCode.Success)
}


object  HttpTranslator extends IOApp {
  import   pab.HttpStreamingModel._

        val httpStream: Stream[IO, String] = Stream.resource(Blocker[IO]).flatMap { blocker =>
          val textStream = io.file.readAll[IO](java.nio.file.Paths.get("ascii.txt"), blocker, 4096)

          val ioResp = Http4sFinal.app[IO].run(Request(POST, Uri("/translate"), textStream)) // ioResp: cats.effect.IO[Response[cats.effect.IO]] = <function1>
          Stream.eval(ioResp) // => Stream[F, Response[F]]
                .flatMap(_.body) // => Stream[F, Byte]
                .through(fs2.text.utf8Decode) // => Stream[F, String]
        }

  override def run(args: List[String]): IO[ExitCode] = {
    val s: IO[String] = httpStream.compile.toList // => F[List[String]]
                                  .map(_.mkString("\n"))
    println(s.unsafeRunSync() )
    IO(ExitCode.Success)
  }
}
