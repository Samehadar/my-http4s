package pab

import cats.Monad

import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import fs2._





object Translator {



  val translate: Map[String, String] = HashMap(
    "one" -> "uno",
    "Hello" -> "Hola",
    )

  def future(text: String): Future[String] = Future {
    Thread.sleep(100)
     translate.getOrElse(text, text)
  }

  def effect[F[_]: Monad](source: String): F[String] = {
    Monad[F].pure {
      val salutation = source.split(",")(0)
      println("Translating " + salutation)
      val translated = Translator.translate.getOrElse(salutation, salutation)
      source.replace(salutation, translated)
    }
  }

    def streaming[F[_]: Monad](body: Stream[F, String]): Stream[F, String] = {
      // Just  to upper case. Easier than translating :)
      body.map(_.toUpperCase())
    }
}
