package com.lgajowy.services

import cats.effect.{ IO, Resource }
import com.lgajowy.domain.errors.{ FileNotFoundError, NotADirectory }
import com.lgajowy.domain.{ Directory, DirectoryPath, FileContents, FilePath }

import java.io.File
import java.nio.file.Files
import scala.io.{ BufferedSource, Source }
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.Using

trait FileReader[F[_]] {
  def readFile(file: File): F[FileContents]

  def readDirectory(directory: DirectoryPath): F[Directory]

  def findAllFilesRecursively(directory: Directory): F[List[File]]
}

object FileReader {


  // TODO: MonadThrow is enough??? (raise error)
  def makeIO(): FileReader[IO] = new FileReader[IO] {
    override def readDirectory(directory: DirectoryPath): IO[Directory] =
      for {
        dir <- IO.delay {
          new File(directory.path)
        }

        _ <- if (!dir.exists())
          IO.raiseError(FileNotFoundError(s"File with path ${directory.path} does not exist or can't be read."))
        else if (!dir.isDirectory)
          IO.raiseError(NotADirectory(s"File with path ${directory.path} is not a directory"))
        else IO.unit

      } yield Directory(dir)

    // TODO: Should this be "blocking"???
    override def findAllFilesRecursively(directory: Directory): IO[List[File]] = {
      IO.blocking {
        Files
          .walk(directory.value.toPath)
          .iterator()
          .asScala
          .filter(Files.isRegularFile(_))
          .map(_.toFile)
          .toList
      }
    }

    override def readFile(file: File): IO[FileContents] =
      Resource
        .fromAutoCloseable(IO(Source.fromFile(file, "UTF-8")))
        .use(
          source => IO(source.getLines().toSeq).map(FileContents(FilePath(file.getAbsolutePath), _))
        )
  }
}
