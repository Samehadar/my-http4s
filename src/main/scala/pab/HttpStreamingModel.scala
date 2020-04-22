package pab

import fs2.Stream

object HttpStreamingModel {
  abstract class Method

  object GET extends Method
  object POST extends Method

  abstract class Status

  object OK extends Status
  object NotFound extends Status

  case class Uri(uri: String)
  case class Request[F[_]] (
                       method: Method,
                       uri: Uri,
                       body: Stream[F, Byte]
                     )

  case class Response[F[_]] (
                        status: Status,
                        body: Stream[F, Byte]
                      )
}
