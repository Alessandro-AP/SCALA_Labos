package Chat

import Chat.Token._
import Chat.Tokenized
import Utils.Dictionary.dictionary
import Utils.SpellCheckerService
import Utils.SpellCheckerImpl

class TokenizerService(spellCheckerSvc: SpellCheckerService):
  /**
    * Separate the user's input into tokens
    * @param input The user's input
    * @return A Tokenizer which allows iteration over the tokens of the input
    */
  //TODO demander pour char special à enlever
  def tokenize(input: String): Tokenized =
    new TokenizedImpl(
      input.replaceAll("[-+^:.,!?*]", "")
        .split("[\\s'’]+")
        .map( word => createToken( spellCheckerSvc.getClosestWordInDictionary(word) ))
    )

  private def createToken(word : String): (String, Token) = word -> findToken(word)

  //TODO demander pour Token -> BAD
  private def findToken(word: String): Token = word match {
    case "bonjour" => BONJOUR
    case "je" => JE
    case "svp" => SVP
    case "assoiffe" => ASSOIFFE
    case "affame" => AFFAME
    case "etre" => ETRE
    case "vouloir" => VOULOIR
    case "et" => ET
    case "ou" => OU
    case "biere" | "croissant" => PRODUCT
    case "EOL" => EOL
    case _ if word.charAt(0) == '_' => PSEUDO
    case _ if word forall Character.isDigit => NUM
    case _ => UNKNOWN
  }

end TokenizerService
