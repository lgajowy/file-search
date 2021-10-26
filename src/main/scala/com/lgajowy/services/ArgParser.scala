package com.lgajowy.services

import cats.{ Applicative, ApplicativeThrow }
import com.lgajowy.domain.DirectoryPath
import com.lgajowy.domain.errors.MissingDirectoryPathArgument

trait ArgParser[F[_]] {
  def parseDirectoryPath(args: List[String]): F[DirectoryPath]
}

object ArgParser {
  def make[F[_]: ApplicativeThrow](): ArgParser[F] = new ArgParser[F] {
    override def parseDirectoryPath(args: List[String]): F[DirectoryPath] =
      if (args.length < 1) {
        ApplicativeThrow[F]
          .raiseError(MissingDirectoryPathArgument("Please enter the directory path as a command line argument"))
      } else {
        // TODO: can I use "pure" here? It seems to have no side effects but what if I'm missing something?
        Applicative[F].pure(DirectoryPath(args.head))
      }
  }
}
