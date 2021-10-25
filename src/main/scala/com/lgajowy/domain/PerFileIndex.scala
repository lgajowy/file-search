package com.lgajowy.domain

case class PerFileIndex(path: FilePath, words: Set[String]) {
  def contains(word: String): Boolean = words.contains(word)
  def totalWords = words.size
}
