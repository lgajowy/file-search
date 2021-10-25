package com.lgajowy.services

import cats.effect.IO
import com.lgajowy.domain.errors.{FileNotFoundError, NotADirectory}
import com.lgajowy.domain.{Directory, DirectoryPath}

import java.io.File

trait FileReader[F[_]] {
  def readDirectory(directory: DirectoryPath): F[Directory]
}

object FileReader {

  def makeIO(): FileReader[IO] = new FileReader[IO] {
    override def readDirectory(directory: DirectoryPath): IO[Directory] =
      for {
        dir <- IO.delay { new File(directory.path) }

        _ <- if (!dir.exists())
          IO.raiseError(FileNotFoundError(s"File with path ${directory.path} does not exist or can't be read."))
        else if (!dir.isDirectory)
          IO.raiseError(NotADirectory(s"File with path ${directory.path} is not a directory"))
        else IO.unit

      } yield Directory(dir)

  }
}
