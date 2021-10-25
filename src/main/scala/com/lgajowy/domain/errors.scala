package com.lgajowy.domain

import scala.util.control.NoStackTrace

object errors {
  case class FileNotFoundError(message: String) extends NoStackTrace
  case class NotADirectory(message: String) extends NoStackTrace
}
