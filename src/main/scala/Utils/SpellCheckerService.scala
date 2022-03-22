package Utils

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
  // TODO - Part 1 Step 2
  def stringDistance(s1: String, s2: String): Int =
    def min(a: Int, b: Int, c: Int) = Math.min(Math.min(a, b), c)

    def levenshtein(s1: List[Char], s2: List[Char]): Int = (s1, s2) match {
      case (_, Nil) => s1.length
      case (Nil, _) => s2.length
      case (h1::t1, h2::t2) => min(levenshtein(t1, s2) + 1, levenshtein(s1, t2) + 1,
        levenshtein(t1, t2) + (if (h1 == h2) 0 else 1))
    }
    levenshtein(s1.toList, s2.toList)
    // TODO check if takes too much time, otherwise try the algorithm with cache
//    val memo = scala.collection.mutable.Map[(List[Char], List[Char]), Int]()
//    def sd(s1: List[Char], s2: List[Char]): Int = {
//      if !memo.contains((s1, s2)) then memo((s1, s2)) = levenshtein(s1, s2)
//      memo((s1, s2))
//    }
//    sd(s1.toList, s2.toList)

  // TODO - Part 1 Step 2
  def getClosestWordInDictionary(misspelledWord: String): String = dictionary(misspelledWord)
end SpellCheckerImpl
