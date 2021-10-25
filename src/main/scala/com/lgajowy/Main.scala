package com.lgajowy

import cats.Traverse
import cats.effect.{ExitCode, IO, IOApp}
import com.lgajowy.domain.{PerFileIndex, Phrase, Result}
import com.lgajowy.services.{ArgParser, FileReader, IndexBuilder, SearchTool}

import java.io.File

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val fileReader = FileReader.makeIO()
    val argParser = ArgParser.makeIO()
    val indexBuilder = IndexBuilder.makeIO()
    val searchTool = SearchTool.makeIO()

    Program(fileReader, argParser, indexBuilder, searchTool).run(args)
  }
}

case class Program(
  fileReader: FileReader[IO],
  argParser: ArgParser[IO],
  indexBuilder: IndexBuilder[IO],
  searchTool: SearchTool[IO]
) {
  def run(args: List[String]): IO[ExitCode] = {
    for {
      directoryPath <- argParser.parseDirectoryPath(args)
      directory <- fileReader.readDirectory(directoryPath)
      filesToSearch <- fileReader.findAllFilesRecursively(directory)
      indexes: IO[List[PerFileIndex]] = Traverse[List].traverse(filesToSearch)((file: File) => {
        for {
          contents <- fileReader.readFile(file)
          perFileIndex <- indexBuilder.buildIndexForFileContents(contents)
        } yield perFileIndex
      })
      _ <- indexes.flatMap(searchLoopStep).foreverM
    } yield ExitCode.Success
  }

  def searchLoopStep(indexes: List[PerFileIndex]): IO[Unit] = {
    for {
      _ <- IO.println(s"search> ")
      phrase <- IO.readLine.map(Phrase(_))
      searchResults <- Traverse[List].traverse(indexes)(searchTool.search(phrase, _))
      top10Results = searchResults.sortBy(_.score.value).take(10)
      _ <- IO.println(toPrettyOutput(top10Results))
    } yield ()
  }

  private def toPrettyOutput(results: List[Result]): String =
    results.map(res => s"${res.path.value} : ${res.score.value}%").mkString("\n")
}
