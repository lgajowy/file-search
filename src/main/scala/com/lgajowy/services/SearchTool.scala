package com.lgajowy.services

import cats.effect.IO
import com.lgajowy.domain.{PerFileIndex, Phrase, RankingValue, Result}

trait SearchTool[F[_]] {
  def search(phrase: Phrase, index: PerFileIndex): F[Result]
}


// TODO: again - should i wrap it in F (IO in this case)???
object SearchTool {
  def makeIO(): SearchTool[IO] = new SearchTool[IO] {
    override def search(phrase: Phrase, index: PerFileIndex): IO[Result] = IO.delay {
      val matchingWordsCount = phrase.value.count(index.contains)
      Result(index.path, RankingValue(matchingWordsCount * 100d / index.totalWords))
    }
  }
}
