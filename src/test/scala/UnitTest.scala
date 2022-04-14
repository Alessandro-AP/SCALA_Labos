import org.scalatest._
import flatspec.AnyFlatSpec
import matchers.should._
import Chat.{AnalyzerService, Parser, TokenizerService}
import Data.*
import Utils.{Dictionary, SpellCheckerImpl, SpellCheckerService}

class UnitTest extends AnyFlatSpec with Matchers {

  val spellCheckerSvc: SpellCheckerService = SpellCheckerImpl(Dictionary.dictionary)
  val tokenizerSvc: TokenizerService = TokenizerService(spellCheckerSvc)
  val analyzerSvc: AnalyzerService = AnalyzerService(ProductImpl(), AccountImpl())
  val session: Session = SessionImpl().create()

  "Analyzer" should "process EtatAme" in {
    var req = "Bonjour, je suis assoiffé !"
    var tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    var expr = Parser(tokenized).parsePhrases()
    var actual = analyzerSvc.reply(session)(expr)
    var expected = "Eh bien, la chance est de votre côté, car nous offrons les meilleures bières de la région !"
    actual shouldBe expected

    req = "Bonjour, je suis affamé !"
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
    actual shouldBe expected
  }

  it should "ask for identification" in {
    var req = "J’aimerais commander 3 bières PunkIPAs !"
    var tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    var expr = Parser(tokenized).parsePhrases()
    var actual = analyzerSvc.reply(session)(expr)
    val expected = "Veuillez d'abord vous identifier."
    actual shouldBe expected

    req = "J'aimerais connaitre mon solde."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    actual shouldBe expected
  }

  it should "process user requests" in {
    var req = "Bonjour, je suis _Michel."
    var tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    var expr = Parser(tokenized).parsePhrases()
    var actual = analyzerSvc.reply(session)(expr)
    var expected = "Hola, michel!"
    actual shouldBe expected

    req = "Combien coûte 1 bière PunkIPA ?"
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Cela coûte CHF 3.0."
    actual shouldBe expected

    req = "J'aimerais connaitre mon solde."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Le montant actuel de votre solde est de CHF 30.0."
    actual shouldBe expected

    req = "Je veux commander 2 bières PunkIPAs et 1 bière Ténébreuse."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 2 biere punkipa et 1 biere tenebreuse ! Cela coûte CHF 10.0 et votre nouveau solde est de CHF 20.0."
//    expected = "Voici donc 2 punkipa et 1 tenebreuse ! Cela coûte CHF 10.0 et votre nouveau solde est de CHF 20.0."
    actual shouldBe expected

    req = "Je voudrais commander 1 croissant."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 1 croissant maison ! Cela coûte CHF 2.0 et votre nouveau solde est de CHF 18.0."
    actual shouldBe expected
  }

  it should "process multiple users" in {
    var req = "Bonjour, je m'appelle _Bob."
    var tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    var expr = Parser(tokenized).parsePhrases()
    var actual = analyzerSvc.reply(session)(expr)
    var expected = "Hola, bob!"
    actual shouldBe expected

    req = "J'aimerais commander 4 bieres JackHammer"
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 4 biere jackhammer ! Cela coûte CHF 12.0 et votre nouveau solde est de CHF 18.0."
//    expected = "Voici donc 4 jackhammer ! Cela coûte CHF 12.0 et votre nouveau solde est de CHF 18.0."
    actual shouldBe expected

    req = "Bonjour, je suis _Alice."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Hola, alice!"
    actual shouldBe expected

    req = "Je suis affamé !"
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
    actual shouldBe expected

    req = "Je veux commander 2 croissants cailler."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 2 croissant cailler ! Cela coûte CHF 4.0 et votre nouveau solde est de CHF 26.0."
    actual shouldBe expected

    req = "Je veux connaître mon solde."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Le montant actuel de votre solde est de CHF 26.0."
    actual shouldBe expected

    req = "je suis _Bob."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Hola, bob!"
    actual shouldBe expected

    req = "Je veux connaître mon solde."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Le montant actuel de votre solde est de CHF 18.0."
    actual shouldBe expected

    req = "J'aimerais commander 18 bières Farmer."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 18 biere farmer ! Cela coûte CHF 18.0 et votre nouveau solde est de CHF 0.0."
//    expected = "Voici donc 18 farmer ! Cela coûte CHF 18.0 et votre nouveau solde est de CHF 0.0."
    actual shouldBe expected
  }

  it should "process OU request correctly" in {
    var req = "Quel est le prix de 1 bière PunkIPA ou 1 croissant ?"
    var tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    var expr = Parser(tokenized).parsePhrases()
    var actual = analyzerSvc.reply(session)(expr)
    var expected = "Cela coûte CHF 2.0."
    actual shouldBe expected

    req = "je suis _Scala."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Hola, scala!"
    actual shouldBe expected

    req = "J'aimerais commander 1 bière wittekop ou 1 croissant"
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 1 biere wittekop ! Cela coûte CHF 2.0 et votre nouveau solde est de CHF 28.0."
//    expected = "Voici donc 1 wittekop ! Cela coûte CHF 2.0 et votre nouveau solde est de CHF 28.0."
    actual shouldBe expected

    req = "J'aimerais commander 1 bière Ténébreuse ou 1 croissant cailler ou 1 biere"
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 1 biere boxer ! Cela coûte CHF 1.0 et votre nouveau solde est de CHF 27.0."
//    expected = "Voici donc 1 boxer ! Cela coûte CHF 1.0 et votre nouveau solde est de CHF 27.0."
    actual shouldBe expected
  }

  it should "process ET request correctly" in {
    var req = "Combien coûtent 4 bières Farmers et 6 croissants cailler?"
    var tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    var expr = Parser(tokenized).parsePhrases()
    var actual = analyzerSvc.reply(session)(expr)
    var expected = "Cela coûte CHF 16.0."
    actual shouldBe expected

    req = "je suis _President."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Hola, president!"
    actual shouldBe expected

    req = "J'veux commander 2 bières Farmers et 1 bière Jackhammer."
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 2 biere farmer et 1 biere jackhammer ! Cela coûte CHF 5.0 et votre nouveau solde est de CHF 25.0."
//    expected = "Voici donc 2 farmer et 1 jackhammer ! Cela coûte CHF 5.0 et votre nouveau solde est de CHF 25.0."
    actual shouldBe expected

    req = "J'veux commander 2 bières Farmers et 1 bière Jackhammer et 1 croissant"
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 2 biere farmer et 1 biere jackhammer et 1 croissant maison ! Cela coûte CHF 7.0 et votre nouveau solde est de CHF 18.0."
//    expected = "Voici donc 2 farmer et 1 jackhammer et 1 croissant maison ! Cela coûte CHF 7.0 et votre nouveau solde est de CHF 18.0."
    actual shouldBe expected
  }

  it should "process mix of ET and OU correctly" in {
    var req = "Bonjour, je m'appelle _Compliqué."
    var tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    var expr = Parser(tokenized).parsePhrases()
    var actual = analyzerSvc.reply(session)(expr)
    var expected = "Hola, compliqué!"
    actual shouldBe expected

    req = "J'aimerais commander 1 biere punkipa et 1 biere boxer ou 1 biere farmer ou 1 biere tenebreuse et 1 biere boxer"
    tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    expr = Parser(tokenized).parsePhrases()
    actual = analyzerSvc.reply(session)(expr)
    expected = "Voici donc 1 biere farmer et 1 biere boxer ! Cela coûte CHF 2.0 et votre nouveau solde est de CHF 28.0."
    actual shouldBe expected
  }

}