package com.lgajowy

import cats.effect.{ ExitCode, IO, IOApp }
import com.lgajowy.services.{ ArgParser, FileReader }

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val fileReader = FileReader.makeIO()
    val argParser = ArgParser.makeIO()

    for {
      directoryPath <- argParser.parseDirectoryPath(args)
      directory <- fileReader.readDirectory(directoryPath)
      filesToSearch <- fileReader.findAllFilesRecursively(directory)
      _ <- IO.println(filesToSearch.map(_.getPath))
    } yield ExitCode.Success
  }
}
