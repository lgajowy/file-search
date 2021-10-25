package com.lgajowy

import cats.Traverse

import cats.effect.{ ExitCode, IO, IOApp }
import com.lgajowy.services.{ ArgParser, FileReader, IndexBuilder, PerFileIndex }

import java.io.File

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val fileReader = FileReader.makeIO()
    val argParser = ArgParser.makeIO()
    val indexBuilder = IndexBuilder.makeIO()

    for {
      directoryPath <- argParser.parseDirectoryPath(args)
      directory <- fileReader.readDirectory(directoryPath)
      filesToSearch <- fileReader.findAllFilesRecursively(directory)
      index: IO[List[PerFileIndex]] = Traverse[List].traverse(filesToSearch)((file: File) => {
        for {
          contents <- fileReader.readFile(file)
          perFileIndex <- indexBuilder.buildIndexForFileContents(contents)
        } yield perFileIndex
      })

      _ <- IO.println(filesToSearch)
    } yield ExitCode.Success
  }
}
