package pab

object HttpModel {
  abstract class Method

  object GET extends Method
  object POST extends Method

  abstract class Status

  object OK extends Status
  object NotFound extends Status

  case class Uri(uri: String)
  case class Request (
                       method: Method,
                       uri: Uri,
                       body: String
                     )

  case class Response (
                        status: Status,
                        body: String = ""
                      )
}
