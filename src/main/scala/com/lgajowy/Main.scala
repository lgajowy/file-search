package com.lgajowy

import cats.Traverse
import cats.effect.{ ExitCode, IO, IOApp }
import com.lgajowy.domain.{ Phrase, Result }
import com.lgajowy.services.{ ArgParser, FileReader, IndexBuilder, PerFileIndex, SearchTool }

import java.io.File
import scala.io.StdIn.readLine

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val fileReader = FileReader.makeIO()
    val argParser = ArgParser.makeIO()
    val indexBuilder = IndexBuilder.makeIO()
    val searchTool = SearchTool.makeIO()

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
      _ <- indexes.flatMap(searchLoopStep(searchTool, _)).foreverM
    } yield ExitCode.Success
  }

  def searchLoopStep(searchTool: SearchTool[IO], indexes: List[PerFileIndex]): IO[Unit] = {
    val topResultsToList = 10

    println(s"search> ")
    val phrase = Phrase(readLine())
    Traverse[List]
      .traverse(indexes)(searchTool.search(phrase, _))
      .map(allResults => { allResults.sortBy(_.score.value).take(topResultsToList) })
      .flatMap(results => IO.println(toPrettyOutput(results)))
  }

  private def toPrettyOutput(results: List[Result]): String =
    results.map(res => s"${res.path.value} : ${res.score.value}%").mkString("\n")
}
