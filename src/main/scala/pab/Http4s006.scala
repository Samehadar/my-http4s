package pab

import scala.concurrent.ExecutionContext.Implicits.global
import cats.{Monad, StackSafeMonad, SemigroupK }
import cats.data.Kleisli
import cats.implicits._

import scala.concurrent.Future

object Http4s006 {
  import HttpModel._
  // HTTP is just a Kleisli function from a request to a ploymorphic effect of a response
  type Http[F[_]] = Kleisli[F, Request, Response]
  
  // An HTTP application is just HTTP in a future effect
  type HttpApp = Http[Future]


  // HTTP routes are just HTTP in a future option
  case class FutureOption[A](value: Future[Option[A]])
  type HttpRoutes = Http[FutureOption]

  object HttpRoutes {
    def of(pf: PartialFunction[Request, Future[Response]]) : HttpRoutes =
      Kleisli(req  =>
                FutureOption(Future.sequence(
                  Option.option2Iterable(
                  pf.lift(req)
                  )).map{ i:Iterable[Response] => i.headOption } ))
  }

  implicit val monadForFutureOption: Monad[FutureOption] =
    new Monad[FutureOption] with StackSafeMonad[FutureOption] {
      def pure[A](x: A) = FutureOption(Future.successful(Some(x)))
      def flatMap[A, B](fa: FutureOption[A])(f: A => FutureOption[B]): FutureOption[B] = {
        FutureOption(fa.value.flatMap {
          case Some(a) => f(a).value
          case None => Future.successful(None) })}}

  implicit val SemigroupForFutureOption: SemigroupK[FutureOption]  =
    new SemigroupK[FutureOption] {
      override def combineK[A](x: FutureOption[A],
                               y: FutureOption[A]): FutureOption[A] = {
        FutureOption(x.value.flatMap { case Some(a) => Future.successful(Some(a))
        case None => y.value
        })
      }
    }


  def hello(theUri: Uri): HttpRoutes = HttpRoutes.of {
    case Request(POST, uri, name) if uri == theUri =>
      Future.successful(Response(OK, s"Hello, $name!"))
  }

  def translate[F[_]: Monad](app: Http[F]): Http[F] = for {
     resp <- app
    tx <- Kleisli.liftF(Translator.effect[F](resp.body))
  }  yield resp.copy(body = tx)


  def seal(routes: HttpRoutes)(req: Request): Future[Response] = {
    routes.run(req).value.map  {
      case Some(a) => a
      case None => Response(NotFound)
    }
  }

  def log[F[_]: Monad](app: Http[F]): Http[F] =
    app.mapF(Monad[F].pure(println("v006 TRANSLATING")) *> _)

  lazy val en: HttpRoutes = hello(Uri("/hello"))
  lazy val es: HttpRoutes = log(translate(hello(Uri("/hola"))))

  lazy val app = seal(es.combineK(en))(_)

}

