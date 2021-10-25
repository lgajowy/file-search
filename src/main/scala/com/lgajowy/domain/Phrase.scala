package com.lgajowy.domain

case class Phrase private (value: Set[String])

object Phrase {
  def apply(sentence: String): Phrase = new Phrase(
    sentence
      .split(" ")
      .map(_.trim)
      .map(_.toLowerCase)
      .toSet
  )
}
