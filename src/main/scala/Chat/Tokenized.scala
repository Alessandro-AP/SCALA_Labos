// SCALA - Labo 4
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 19.06.22

package Chat

import Chat.Token.EOL

trait Tokenized:
  /**
    * Get the next token of the user input, or EOL if there is no more token.
    * @return a tuple that contains the string value of the current token, and the identifier of the token
    */
  def nextToken(): (String, Token)

class TokenizedImpl(val tokens: Array[(String, Token)]) extends Tokenized:
  private var index = -1
  
  def nextToken(): (String, Token) =
    if index + 1 < tokens.length then 
      index += 1
      tokens(index)
    else 
      "EOL" -> EOL
      
end TokenizedImpl
