package pab

import fs2.{Stream, io, text}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import pab.HttpModel._

import scala.concurrent.ExecutionContext


class Http4sTest extends AsyncFlatSpec with Matchers {
  "A httpApp v0.0.1 function" should "take a request and return a response" in {
    val req = Request(POST, Uri("/hello"), "Paul")
    Http4s001.helloWorld(req).body should be("Hello, Paul!")
  }

  "A Translator" should "provide a result in the future"  in {
   Translator.future("one").map { _ should be ("uno") }
  }

  "A httpApp v0.0.2 function" should "take a request and return a future response" in {
    val req = Request(POST, Uri("/translate"), "one")
    Http4s002.app(req).map { _.body  should be("uno") }

  }

  "A httpApp v0.0.3 function" should  "combine httpRoute partial functions" in {
    val req = Request(POST, Uri("/hello"), "Paul")
    Http4s003.app(req).map { _.body  should be("Hello, Paul!") }

    val req2 = Request(POST, Uri("/hola"), "Paul")
    Http4s003.app(req2).map { _.body  should be("Hola, Paul!") }
  }

  "A httpApp v0.0.3 function" should  "throw match error if route not matched" in {
    val req = Request(POST, Uri("/guten-tag"), "Paul")
    assertThrows[MatchError] { Http4s003.app(req) }
  }

  "A httpApp v0.0.4 function" should  "return NotFound response if route not matched" in {
    val req = Request(POST, Uri("/guten-tag"), "Paul")
    Http4s004.app(req).map { _.status  should be(NotFound) }
  }

  "A httpApp v0.0.5 function" should "not compile since OptionFuture is not a Monad" in {
    assertDoesNotCompile(
      """Http4s005.translate(Http4s005.hello(Uri("/hola")))"""
    )
  }

  "A httpApp v0.0.6 function" should "be composable" in {
    val req = Request(POST, Uri("/hola"), "Paul")
    val req2 = Request(POST, Uri("/hello"), "Paul")
    val req3 = Request(POST, Uri("/guten-tag"), "Paul")

    Http4s006.app(req).map {_ should be (Response(OK, "Hola, Paul!"))  }
    Http4s006.app(req2).map {_ should be (Response(OK, "Hello, Paul!"))  }
    Http4s006.app(req3).map {_ should be (Response(NotFound))  }
  }

  "A httpApp v0.0.8 function" should "be composable" in {
    val req = Request(POST, Uri("/hola"), "Boston")
    val req2 = Request(POST, Uri("/hello"), "Boston")
    val req3 = Request(POST, Uri("/guten-tag"), "Boston")

    Http4s008.app(req).unsafeRunSync should be (Response(OK, "Hola, Boston!"))
    Http4s008.app(req2).unsafeRunSync should be(Response(OK, "Hello, Boston!"))
    Http4s008.app(req3).unsafeRunSync should be (Response(NotFound))
  }

  "A httpApp Final function" should "take a request stream and return a response stream" in {
    import cats.effect.{Blocker, IO}

    implicit val contextShift = IO.contextShift(ExecutionContext.global)

      import pab.HttpStreamingModel._

      val httpStream: Stream[IO, String] = Stream.resource(Blocker[IO]).flatMap { blocker =>
      val textStream = io.file.readAll[IO](java.nio.file.Paths.get("ascii.txt"), blocker, 4096)

      val ioResp = Http4sFinal.app[IO].run(Request(POST, Uri("/translate"), textStream)) // ioResp: cats.effect.IO[Response[cats.effect.IO]] = <function1>
      Stream.eval(ioResp) // => Stream[F, Response[F]]
            .flatMap(_.body) // => Stream[F, Byte]
            .through(fs2.text.utf8Decode) // => Stream[F, String]
      } 
    
      httpStream
          .through(text.lines)
          .take(1)
          .compile.toList
          .map(_.mkString("\n"))
          .unsafeRunSync() should be ("\"NOTHING TO DO?\"")
  }
}
