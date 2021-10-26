package com.lgajowy.services

import cats.Applicative
import com.lgajowy.domain.{ FileContents, PerFileIndex }

trait IndexBuilder[F[_]] {
  def buildIndexForFileContents(lines: FileContents): F[PerFileIndex]
}

object IndexBuilder {
  def make[F[_]: Applicative](): IndexBuilder[F] = new IndexBuilder[F] {
    override def buildIndexForFileContents(contents: FileContents): F[PerFileIndex] = {
      Applicative[F].pure {
        val lines = contents.lines
        val wordSet = lines
          .flatMap(
            _.split(" ")
              .map(_.trim)
              .map(_.toLowerCase)
          )
          .toSet

        PerFileIndex(contents.filePath, wordSet)
      }
    }
  }
}
