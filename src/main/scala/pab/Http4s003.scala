package pab

import scala.concurrent.Future

object Http4s003 {
  import HttpModel._

  type HttpApp = Request => Future[Response]
  type HttpRoute = PartialFunction[Request, Future[Response]]

  def combine(x: HttpRoute, y: HttpRoute): HttpApp = x orElse y

  val helloWorld: HttpRoute = {
    case Request(POST, Uri("/hello"), name)  =>
      Future.successful(Response(OK, s"Hello, $name!"))
  }

  val holaMundo: HttpRoute = {
    case Request(POST, Uri("/hola"), name)  =>
      Future.successful(Response(OK, s"Hola, $name!"))
  }

  val app: HttpApp = combine(holaMundo, helloWorld)
}
