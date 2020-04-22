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


  def main(args: Array[String]): Unit = {
    val r = Request(POST, Uri("/hello"), "Paul")
    val a = helloWorld
    println(a(r).body)
  }
}
