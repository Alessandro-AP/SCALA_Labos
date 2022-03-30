// SCALA - Labo 1
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 27.03.22

import scala.io.StdIn
import Chat.{Token, TokenizerService}
import Utils.{ClinksCalculator, Dictionary, SpellCheckerService, SpellCheckerImpl}

object MainTokenizer:
  def main(args: Array[String]): Unit =
    val spellCheckerSvc: SpellCheckerService = SpellCheckerImpl(Dictionary.dictionary)
    val tokenizerSvc: TokenizerService = TokenizerService(spellCheckerSvc)

    println("Bienvenue au Chill-Out !")

    while true do
      // Convert the user input to lower case, then take an action depending on the value.
      print("> ")
      StdIn.readLine.toLowerCase match
        case "quitter" => println("Adieu."); System.exit(0)
        case "santé !" =>
          for i <- 2 to 6 do
            println(s"Nombre de *clinks* pour un santé de $i personnes : ${ClinksCalculator.calculateCombination(i, 2)}.")
        case s =>
          if s.isEmpty then
            println("La commande ne peut pas être vide!!!")
          else
            // Start benchmark time used for measuring the execution time
            val startTime = System.currentTimeMillis()
            // Tokenize the user input.
            val tokenizer = tokenizerSvc.tokenize(s)
            // Display every token.
            while
              val currentToken: (String, Token) = tokenizer.nextToken()
              println(currentToken)
              // Loop condition
              currentToken._2 != Token.EOL
            do ()
            println("Execution time: " + (System.currentTimeMillis() - startTime) + " milliseconds")
            println("============================================")

    end while
  end main
end MainTokenizer
