package Chat

import Chat.Token.*
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
      input.replaceAll("[-+.^:,]", "")
        .split("[\\s']+")
        .map( word => (dictionary(word), ))
    )
end TokenizerService
