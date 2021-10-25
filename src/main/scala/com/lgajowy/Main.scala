package com.lgajowy

import cats.effect.{ExitCode, IO, IOApp}
import com.lgajowy.domain.DirectoryPath
import com.lgajowy.services.FileReader

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val fileReader = FileReader.makeIO()

    for {
      _ <- if (args.length < 1) IO.raiseError(new IllegalArgumentException("Please enter the directory path"))
      else IO.unit
      path = DirectoryPath(args.head)
      directory <- fileReader.readDirectory(path)
      _ <- IO.println(directory)
    } yield ExitCode.Success
  }
}
