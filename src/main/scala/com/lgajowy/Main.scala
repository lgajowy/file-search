package com.lgajowy

import cats.effect._

object Main extends IOApp {

  private case class DirectoryPath(path: String)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- if (args.length < 1) IO.raiseError(new IllegalArgumentException("Please enter the directory path"))
      else IO.unit
      path = DirectoryPath(args.head)
      _ <- IO.println(path)
    } yield ExitCode.Success
}
