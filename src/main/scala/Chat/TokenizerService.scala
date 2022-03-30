// SCALA - Labo 1
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 27.03.22

package Chat

import Chat.Token._
import Chat.Tokenized
import Utils.Dictionary.dictionary
import Utils.SpellCheckerService

class TokenizerService(spellCheckerSvc: SpellCheckerService):
  /**
    * Separate the user's input into tokens
    * @param input The user's input
    * @return A Tokenizer which allows iteration over the tokens of the input
    */
  def tokenize(input: String): Tokenized =
    new TokenizedImpl(
      input.replaceAll("[-+/<>%&#$^~:@{}|.!?*]", "")
        .split("[\\s'’,;]+")
        .map(word => createToken(dictionary.getOrElse(word, spellCheckerSvc.getClosestWordInDictionary(word))))
    )

  /**
    * From the word in parameter, create a tuple of this word and its corresponding token.
    * @return the tuple (word, Token)
    */
  private def createToken(word : String): (String, Token) = word -> getTokenByWord(word)

  /**
    * Retrieve the corresponding token of the word passed in parameter.
    */
  private def getTokenByWord(word: String): Token = word match {
    case "bonjour" => BONJOUR
    case "je" => JE
    case "etre" => ETRE
    case "vouloir" => VOULOIR
    case "assoiffe" => ASSOIFFE
    case "affame" => AFFAME
    case "biere" | "croissant" => PRODUCT
    case "et" => ET
    case "ou" => OU
    case "svp" => SVP
    case "EOL" => EOL
    case _ if word.charAt(0) == '_' => PSEUDO
    case _ if word forall Character.isDigit => NUM
    case _ => UNKNOWN
  }

end TokenizerService
