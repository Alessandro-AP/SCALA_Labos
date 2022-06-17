//// SCALA - Labo 3
//// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
//// Date: 24.05.22
//
//import scala.io.StdIn
//import Utils._
//import Chat.{TokenizerService, Token, UnexpectedTokenException, Parser, AnalyzerService}
//import Services._
//
//import scala.io.StdIn
//
//object MainParser:
//  def main(args: Array[String]): Unit =
//    val spellCheckerSvc: SpellCheckerService = new SpellCheckerImpl(Dictionary.dictionary)
//    val tokenizerSvc: TokenizerService = new TokenizerService(spellCheckerSvc)
//    val productSvc: ProductService = new ProductImpl()
//    val sessionSvc: SessionService = new SessionImpl()
//    val accountSvc: AccountService = new AccountImpl()
//    val analyzerSvc: AnalyzerService = new AnalyzerService(productSvc, accountSvc)
//
//    println("Bienvenue au Chill-Out !")
//
//    val session = sessionSvc.create()
//
//    while true do
//      print("> ")
//      StdIn.readLine.toLowerCase match
//        case "quitter" => println("Adieu."); System.exit(0)
//        case "santé !" =>
//          for i <- 2 to 6 do
//            println(s"Nombre de *clinks* pour un santé de $i personnes : ${ClinksCalculator.calculateCombination(i, 2)}.")
//        case "" => println("Veuillez entrer quelque chose")
//        case s =>
//          try
//            val tokenized = tokenizerSvc.tokenize(s)
//            val expr = Parser(tokenized).parsePhrases()
//            println(analyzerSvc.reply(session)(expr))
//          catch
//            case e: UnexpectedTokenException => println(s"Invalid input. ${e.getMessage}")
//    end while
//  end main
//end MainParser
//
