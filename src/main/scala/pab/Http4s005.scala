package pab

import cats.data.Kleisli
import cats.effect.Sync

import scala.concurrent.Future

object Http4s005 {
  import HttpModel._
  
  // HTTP is just a Kleisli function from a request to a polymorphic effect of a response
  type Http[F[_]] = Kleisli[F, Request, Response]
  
  // An HTTP application is just HTTP in a future effect
  type HttpApp = Http[Future]
  object HttpApp {
    def apply(f: Request => Future[Response]): HttpApp =
      Kleisli(f)
  }

  // HTTP routes are just HTTP in an optional future
  case class OptionFuture[A](value: Option[Future[A]])
  type HttpRoutes = Http[OptionFuture]

  object HttpRoutes {
    def of(pf: PartialFunction[Request, Future[Response]]) : HttpRoutes =
      Kleisli(pf.lift.andThen(OptionFuture(_)))
  }

  def translate[F[_]: Sync](app: Http[F]): Http[F] = for {
     resp <- app
    tx <- Kleisli.liftF(Translator.effect(resp.body))
  }  yield resp.copy(body = tx)

  def hello(theUri: Uri): HttpRoutes = HttpRoutes.of {
    case Request(POST, uri, name) if uri == theUri =>
      Future.successful(Response(OK, s"Hello, $name!"))
  }
}

