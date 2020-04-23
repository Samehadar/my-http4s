package pab

import cats.Functor
import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import cats.implicits._
import fs2._

object Http4sFinal {
  import HttpStreamingModel._

  // HTTP is just a Kleisli function from a request to a polymorphic effect of a response
  type Http[F[_], G[_]] = Kleisli[F, Request[G], Response[G]]
  
  // An HTTP application is just HTTP in an IO effect
  type HttpApp[F[_]] = Http[F, F]

  // HTTP Routes are just HTTP where the response context is an Option monad transformer
  // applied to a polymorphic effect of a response
  type HttpRoutes[F[_]] = Http[OptionT[F, ?], F]
  object HttpRoutes {
    def of[F[_]: Sync](pf: PartialFunction[Request[F], F[Response[F]]]): HttpRoutes[F] =
      Kleisli(req => OptionT(Sync[F].suspend(pf.lift( req).sequence)))
  }

  def seal[F[_]: Functor](routes: HttpRoutes[F]): HttpApp[F] = {
    routes. mapF(_.getOrElse(Response(NotFound, Stream.empty)))
  }

  def app[F[_]: Sync]: HttpApp[F] = seal(HttpRoutes.of {
    case Request(POST, Uri("/translate"), body) =>
      Response(OK, Translator.streaming[F](
        body.through(fs2.text.utf8Decode[F])
        ).through(fs2.text.utf8Encode[F])).pure[F]
  })
}

