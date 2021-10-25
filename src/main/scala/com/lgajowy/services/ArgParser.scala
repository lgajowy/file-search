package com.lgajowy.services

import cats.effect.IO
import com.lgajowy.domain.DirectoryPath
import com.lgajowy.domain.errors.MissingDirectoryPathArgument

trait ArgParser[F[_]] {
  def parseDirectoryPath(args: List[String]): F[DirectoryPath]
}

object ArgParser {
  def makeIO(): ArgParser[IO] = new ArgParser[IO] {
    override def parseDirectoryPath(args: List[String]): IO[DirectoryPath] =
      if (args.length < 1) {
        IO.raiseError(MissingDirectoryPathArgument("Please enter the directory path as a command line argument"))
      } else {
        IO(DirectoryPath(args.head))
      }
  }
}
