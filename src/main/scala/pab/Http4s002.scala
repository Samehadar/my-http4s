package pab

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Http4s002 {
  import HttpModel._

  type HttpApp = Request => Future[Response]

  val app: HttpApp = {
    case Request(POST, Uri("/translate"), text) =>
      Translator.future(text).map(Response(OK, _))
    case _ =>
      Future.successful(Response(NotFound))
  }
}


