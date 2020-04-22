package pab

import cats.Monad
import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import cats.implicits._

object Http4s008 {
  import HttpModel._

  // HTTP is just a Kleisli function from a request to a polymorphic effect of a response
  type Http[F[_]] = Kleisli[F, Request, Response]
  
  // An HTTP application is just HTTP in an IO effect
  type HttpApp = Http[IO]
  object HttpApp {
    def apply(f: Request => IO[Response]): HttpApp =
      Kleisli(f)
  }

  // HTTP Routes are just HTTP where the response context is an Option monad transformer
  // applied to a polymorphic effect of a response
  type HttpRoutes[F[_]] = Http[OptionT[F, ?]]
  object HttpRoutes {
    def of[F[_]: Monad](pf: PartialFunction[Request, F[Response]]): HttpRoutes[F] =
      Kleisli(req => OptionT(pf.lift( req).sequence))
  }

  def hello[F[_]: Monad](theUri: Uri): HttpRoutes[F] = HttpRoutes.of {
    case Request(POST, uri, name) if uri == theUri =>
      Monad[F].pure(Response(OK, s"Hello, $name!"))
  }

  def translate[F[_]: Monad](app: Http[F]): Http[F] = for {
     resp <- app
    tx <- Kleisli.liftF(Translator.effect[F](resp.body))
  }  yield resp.copy(body = tx)
  
  def seal[F[_]: Monad](routes: HttpRoutes[F]): HttpRoutes[F] = {
        routes.combineK(HttpRoutes.of {
          _ => Monad[F].pure(Response(NotFound))
        })
  }

//  def seal2[F[_]: Monad](routes: HttpRoutes[F]): HttpRoutes[F] = {
//    routes.mapF{ _.getOrElse(OptionT.liftF(Monad[F].pure((Response(NotFound))))) }
//  }

  def log[F[_]: Monad](app: Http[F]): Http[F] =
    app.mapF(Monad[F].pure(println("v008 TRANSLATING")) *> _)

  val en: HttpRoutes[IO] = hello(Uri("/hello"))
  val es: HttpRoutes[IO] = translate(log(hello(Uri("/hola"))) )

  val app: Http[IO] = HttpApp(seal(en.combineK(es)).run(_:Request).value.map { _.get })

}

