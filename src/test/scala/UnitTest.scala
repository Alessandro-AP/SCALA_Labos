// SCALA - Labo 2
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 16.04.22

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

  private def parseRequest(req: String) = {
    val tokenized = tokenizerSvc.tokenize(req.toLowerCase)
    val expr = Parser(tokenized).parsePhrases()
    analyzerSvc.reply(session)(expr)
  }

  "Analyzer" should "process EtatAme" in {
    var actual = parseRequest("Bonjour, je suis assoiffé !")
    var expected = "Eh bien, la chance est de votre côté, car nous offrons les meilleures bières de la région !"
    actual shouldBe expected

    actual = parseRequest("Bonjour, je suis affamé !")
    expected = "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
    actual shouldBe expected
  }

  it should "ask for identification" in {
    var actual = parseRequest("J’aimerais commander 3 bières PunkIPAs !")
    val expected = "Veuillez d'abord vous identifier."
    actual shouldBe expected

    actual = parseRequest("J'aimerais connaitre mon solde.")
    actual shouldBe expected
  }

  it should "process user requests" in {
    var actual = parseRequest("Bonjour, je suis _Michel.")
    var expected = "Hola, michel!"
    actual shouldBe expected

    actual = parseRequest("Combien coûte 1 bière PunkIPA ?")
    expected = "Cela coûte CHF 3.0."
    actual shouldBe expected

    actual = parseRequest("J'aimerais connaitre mon solde.")
    expected = "Le montant actuel de votre solde est de CHF 30.0."
    actual shouldBe expected

    actual = parseRequest("Je veux commander 2 bières PunkIPAs et 1 bière Ténébreuse.")
    expected = "Voici donc 2 biere punkipa et 1 biere tenebreuse ! Cela coûte CHF 10.0 et votre nouveau solde est de CHF 20.0."
    actual shouldBe expected

    actual = parseRequest("Je voudrais commander 1 croissant.")
    expected = "Voici donc 1 croissant maison ! Cela coûte CHF 2.0 et votre nouveau solde est de CHF 18.0."
    actual shouldBe expected
  }

  it should "process multiple users" in {
    var actual = parseRequest("Bonjour, je m'appelle _Bob.")
    var expected = "Hola, bob!"
    actual shouldBe expected

    actual = parseRequest("J'aimerais commander 4 bieres JackHammer")
    expected = "Voici donc 4 biere jackhammer ! Cela coûte CHF 12.0 et votre nouveau solde est de CHF 18.0."
    actual shouldBe expected

    actual = parseRequest("Bonjour, je suis _Alice.")
    expected = "Hola, alice!"
    actual shouldBe expected

    actual = parseRequest("Je suis affamé !")
    expected = "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
    actual shouldBe expected

    actual = parseRequest("Je veux commander 2 croissants cailler.")
    expected = "Voici donc 2 croissant cailler ! Cela coûte CHF 4.0 et votre nouveau solde est de CHF 26.0."
    actual shouldBe expected

    actual = parseRequest("Je veux connaître mon solde.")
    expected = "Le montant actuel de votre solde est de CHF 26.0."
    actual shouldBe expected

    actual = parseRequest("je suis _Bob.")
    expected = "Hola, bob!"
    actual shouldBe expected

    actual = parseRequest("Je veux connaître mon solde.")
    expected = "Le montant actuel de votre solde est de CHF 18.0."
    actual shouldBe expected

    actual = parseRequest("J'aimerais commander 18 bières Farmer.")
    expected = "Voici donc 18 biere farmer ! Cela coûte CHF 18.0 et votre nouveau solde est de CHF 0.0."
    actual shouldBe expected
  }

  it should "process OU request correctly" in {
    var actual = parseRequest("Quel est le prix de 1 bière PunkIPA ou 1 croissant ?")
    var expected = "Cela coûte CHF 2.0."
    actual shouldBe expected

    actual = parseRequest("je suis _Scala.")
    expected = "Hola, scala!"
    actual shouldBe expected

    actual = parseRequest("J'aimerais commander 1 bière wittekop ou 1 croissant")
    expected = "Voici donc 1 biere wittekop ! Cela coûte CHF 2.0 et votre nouveau solde est de CHF 28.0."
    actual shouldBe expected

    actual = parseRequest("J'aimerais commander 1 bière Ténébreuse ou 1 croissant cailler ou 1 biere")
    expected = "Voici donc 1 biere boxer ! Cela coûte CHF 1.0 et votre nouveau solde est de CHF 27.0."
    actual shouldBe expected
  }

  it should "process ET request correctly" in {
    var actual = parseRequest("Combien coûtent 4 bières Farmers et 6 croissants cailler?")
    var expected = "Cela coûte CHF 16.0."
    actual shouldBe expected

    actual = parseRequest("je suis _President.")
    expected = "Hola, president!"
    actual shouldBe expected

    actual = parseRequest("J'veux commander 2 bières Farmers et 1 bière Jackhammer.")
    expected = "Voici donc 2 biere farmer et 1 biere jackhammer ! Cela coûte CHF 5.0 et votre nouveau solde est de CHF 25.0."
    actual shouldBe expected

    actual = parseRequest("J'veux commander 2 bières Farmers et 1 bière Jackhammer et 1 croissant")
    expected = "Voici donc 2 biere farmer et 1 biere jackhammer et 1 croissant maison ! Cela coûte CHF 7.0 et votre nouveau solde est de CHF 18.0."
    actual shouldBe expected
  }

  it should "process mix of ET and OU correctly" in {
    var actual = parseRequest("Bonjour, je m'appelle _Compliqué.")
    var expected = "Hola, compliqué!"
    actual shouldBe expected

    actual = parseRequest("J'aimerais commander 1 biere punkipa et 1 biere boxer ou 1 biere farmer ou 1 biere tenebreuse et 1 biere boxer")
    expected = "Voici donc 1 biere farmer et 1 biere boxer ! Cela coûte CHF 2.0 et votre nouveau solde est de CHF 28.0."
    actual shouldBe expected
  }

}