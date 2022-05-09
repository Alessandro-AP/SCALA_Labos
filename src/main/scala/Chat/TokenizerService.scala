// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 09.05.22

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
    TokenizedImpl(
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
    case "me" => ME
    case "mon" => MON
    case "quel" => QUEL
    case "le" => LE
    case "de" => DE
    case "svp" => SVP
    case "assoiffe" => ASSOIFFE
    case "affame" => AFFAME
    case "connaitre" => CONNAITRE
    case "commander" => COMMANDER
    case "solde" => SOLDE
    case "combien" => COMBIEN
    case "couter" | "prix" => PRIX
    case "etre" => ETRE
    case "appeler" => APPELER
    case "vouloir" => VOULOIR
    case "biere" | "croissant" => PRODUCT
    case "maison" | "cailler" | "farmer" | "boxer" | "wittekop" | "punkipa" | "jackhammer" | "tenebreuse" => MARQUE
    case "et" => ET
    case "ou" => OU
    case "EOL" => EOL
    case _ if word.startsWith("_") => PSEUDO
    case _ if word forall Character.isDigit => NUM
    case _ => UNKNOWN
  }

end TokenizerService
