package Chat

import Chat.Token.*
import Utils.SpellCheckerService

trait Tokenized:
  /**
    * Get the next token of the user input, or EOL if there is no more token.
    * @return a tuple that contains the string value of the current token, and the identifier of the token
    */
  def nextToken(): (String, Token)

class TokenizedImpl(val tokens: Array[(String, Token)]) extends Tokenized:
  private var index = -1
  def nextToken(): (String, Token) =
    require(index + 1 < tokens.length, "Buffer overflow")
    index = index + 1
    tokens(index)


end TokenizedImpl
