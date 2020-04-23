package pab

object Http4s001 {

  import HttpModel._

  type HttpApp = Request => Response

  val helloWorld: HttpApp = {
    case Request(POST, Uri("/hello"), name)  =>
      Response(OK, s"Hello, $name!")
    case _ =>
      Response(NotFound)
  }
}
