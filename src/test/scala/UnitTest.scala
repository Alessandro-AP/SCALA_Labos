import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterAll
import Chat.{AnalyzerService, Parser, TokenizerService}
import Data.*
import Utils.{Dictionary, SpellCheckerImpl, SpellCheckerService}

class UnitTest extends AnyFlatSpec with BeforeAndAfterAll {

  val spellCheckerSvc: SpellCheckerService = new SpellCheckerImpl(Dictionary.dictionary)
  val tokenizerSvc: TokenizerService = new TokenizerService(spellCheckerSvc)
  val productSvc: ProductService = new ProductImpl()
  val sessionSvc: SessionService = new SessionImpl()
  val accountSvc: AccountService = new AccountImpl()
  val analyzerSvc: AnalyzerService = new AnalyzerService(productSvc, accountSvc)
  val session: Session = sessionSvc.create()

  "Request" should "match" in {
    val req = "Bonjour, je suis assoiffé !"
    val tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    val parser = new Parser(tokenized)
    val expr = parser.parsePhrases()
    val actual = analyzerSvc.reply(session)(expr)
    val expected = "Eh bien, la chance est de votre côté, car nous offrons les meilleures bières de la région !"
    assert(actual.equals(expected))
  }

  it should "match1" in {
    val req = "Bonjour, je suis affamé !"
    val tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    val parser = new Parser(tokenized)
    val expr = parser.parsePhrases()
    val actual = analyzerSvc.reply(session)(expr)
    val expected = "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
    assert(actual.equals(expected))
  }
  
}