// SCALA - Labo 1
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 27.03.22

package Utils

import scala.collection.mutable

/**
  * Authors: Alessandro Parrino, Daniel Sciarra ◕◡◕
  * Date: 27.03.22
  */
trait SpellCheckerService:
  /**
    * This dictionary is a Map object that contains valid words as keys and their normalized equivalents as values (e.g.
    * we want to normalize the words "veux" and "aimerais" in one unique term: "vouloir").
    */
  val dictionary: Map[String, String]

  /**
    * Calculate the Levenstein distance between two words.
    * @param s1 the first word
    * @param s2 the second word
    * @return an integer value, which indicates the Levenstein distance between "s1" and "s2"
    */
  def stringDistance(s1: String, s2: String): Int

  /**
    * Get the syntactically closest word in the dictionary from the given misspelled word, using the "stringDistance"
    * function. If the word is a number or a pseudonym, this function just returns it.
    * @param misspelledWord the mispelled word to correct
    * @return the closest normalized word from "mispelledWord"
    */
  def getClosestWordInDictionary(misspelledWord: String): String
end SpellCheckerService

class SpellCheckerImpl(val dictionary: Map[String, String]) extends SpellCheckerService:

  // List of authorized words
  private val dictionaryKeys = dictionary.keys.toVector

  def stringDistance(s1: String, s2: String): Int =
    def minimum(a: Int, b: Int, c: Int) = a min b min c

    def levenshtein(s1: List[Char], s2: List[Char]): Int = (s1, s2) match {
      case (_, Nil) => s1.length
      case (Nil, _) => s2.length
      case (h1::t1, h2::t2) if (h1 == h2) => levenshtein(t1, t2)
      case (h1::t1, h2::t2) => 1 + minimum(levenshtein(t1, s2), levenshtein(s1, t2), levenshtein(t1, t2))
    }
    levenshtein(s1.toList, s2.toList)

  def getClosestWordInDictionary(misspelledWord: String): String =
    if (misspelledWord.charAt(0) == '_' || (misspelledWord forall Character.isDigit))
      misspelledWord
    else
      // TODO change implementation (lab 1 correction)
      var closestWord = (dictionaryKeys.head, stringDistance(dictionaryKeys.head, misspelledWord))

      for (i <- 1 until dictionaryKeys.size)
        val dist = stringDistance(dictionaryKeys(i), misspelledWord)

        // For equal distance, we take the word that comes first in the alphabetical order.
        if (dist == closestWord._2 && dictionaryKeys(i) < closestWord._1) || dist < closestWord._2
        then closestWord = (dictionaryKeys(i), dist)

      dictionary(closestWord._1)

end SpellCheckerImpl
