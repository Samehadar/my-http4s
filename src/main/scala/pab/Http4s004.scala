package pab

import scala.concurrent.Future

object Http4s004 {
  import HttpModel._

  type HttpApp = Request => Future[Response]
  type HttpRoutes = Function[Request, Option[Future[Response]]]

  object HttpRoutes {
    def of(pf: PartialFunction[Request, Future[Response]]) : HttpRoutes = pf.lift
  }

  def combine(x: HttpRoutes, y: HttpRoutes): HttpRoutes = req => x(req).orElse(y(req))

  def seal(routes: HttpRoutes): HttpApp = {
    routes.andThen {
      _.getOrElse(Future.successful(Response(NotFound)))
    }
  }

  val helloWorld: HttpRoutes = HttpRoutes.of {
    case Request(POST, Uri("/hello"), name)  =>
      Future.successful(Response(OK, s"Hello, $name!"))
  }

  val holaMundo: HttpRoutes = HttpRoutes.of {
    case Request(POST, Uri("/hola"), name)  =>
      Future.successful(Response(OK, s"Hola, $name!"))
  }

  val app: HttpApp = seal(combine(holaMundo, helloWorld))
}

