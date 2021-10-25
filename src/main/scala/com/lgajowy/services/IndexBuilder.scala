package com.lgajowy.services

import cats.effect.IO
import com.lgajowy.domain.FileContents

trait IndexBuilder[F[_]] {
  def buildIndexForFileContents(lines: FileContents): F[PerFileIndex]
}

object IndexBuilder {
  def makeIO(): IndexBuilder[IO] = new IndexBuilder[IO] {
    override def buildIndexForFileContents(contents: FileContents): IO[PerFileIndex] = {
      IO.delay {
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
