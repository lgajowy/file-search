package com.lgajowy.services

import com.lgajowy.domain.FilePath

case class PerFileIndex(path: FilePath, words: Set[String]) {
  def contains(word: String): Boolean = words.contains(word)
  def totalWords = words.size
}
