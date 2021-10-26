package com.lgajowy.services

import cats.Applicative
import com.lgajowy.domain.{ PerFileIndex, Phrase, RankingValue, Result }

trait SearchTool[F[_]] {
  def search(phrase: Phrase, index: PerFileIndex): F[Result]
}

object SearchTool {
  def make[F[_]: Applicative](): SearchTool[F] = new SearchTool[F] {
    override def search(phrase: Phrase, index: PerFileIndex): F[Result] = Applicative[F].pure {
      val matchingWordsCount = phrase.value.count(index.contains)
      Result(index.path, RankingValue(matchingWordsCount * 100d / index.totalWords))
    }
  }
}
