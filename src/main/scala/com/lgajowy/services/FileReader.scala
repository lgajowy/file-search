package com.lgajowy.services

import cats.effect.{ IO, Resource, Sync }
import cats.syntax.all._
import com.lgajowy.domain.errors.{ FileNotFoundError, NotADirectory }
import com.lgajowy.domain.{ Directory, DirectoryPath, FileContents, FilePath }

import java.io.File
import java.nio.file.Files
import scala.io.Source
import scala.jdk.CollectionConverters.IteratorHasAsScala

trait FileReader[F[_]] {
  def readFile(file: File): F[FileContents]

  def readDirectory(directory: DirectoryPath): F[Directory]

  def findAllFilesRecursively(directory: Directory): F[List[File]]
}

object FileReader {

  def make[F[_]: Sync](): FileReader[F] = new FileReader[F] {
    override def readDirectory(directory: DirectoryPath): F[Directory] = {
      for {
        dir <- Sync[F].delay(new File(directory.path))

        _ <- if (!dir.exists())
          Sync[F]
            .raiseError(FileNotFoundError(s"File with path ${directory.path} does not exist or can't be read."))
        else if (!dir.isDirectory)
          Sync[F].raiseError(NotADirectory(s"File with path ${directory.path} is not a directory"))
        else Sync[F].unit

      } yield Directory(dir)
    }

    override def findAllFilesRecursively(directory: Directory): F[List[File]] = {
      Sync[F].delay {
        Files
          .walk(directory.value.toPath)
          .iterator()
          .asScala
          .filter(Files.isRegularFile(_))
          .map(_.toFile)
          .toList
      }
    }

    override def readFile(file: File): F[FileContents] = {
      Resource
        .fromAutoCloseable(Sync[F].delay { Source.fromFile(file, "UTF-8") })
        .use(source => Sync[F].delay { FileContents(FilePath(file.getAbsolutePath), source.getLines().toSeq) })
    }
  }
}
