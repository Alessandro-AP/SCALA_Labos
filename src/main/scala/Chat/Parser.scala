package Chat

import scala.annotation.tailrec

class UnexpectedTokenException(msg: String) extends Exception(msg) {}

// TODO - step 4
class Parser(tokenized: Tokenized):
  import ExprTree._
  import Chat.Token._

  // Start the process by reading the first token.
  var curTuple: (String, Token) = tokenized.nextToken()

  def curValue: String = curTuple._1
  def curToken: Token = curTuple._2

  /** Reads the next token and assigns it into the global variable curTuple */
  def readToken(): Unit = curTuple = tokenized.nextToken()

  /** "Eats" the expected token and returns it value, or terminates with an error. */
  private def eat(token: Token): String =
    if token == curToken then
      val tmp = curValue
      readToken()
      tmp
    else expected(token)

  /** Complains that what was found was not expected. The method accepts arbitrarily many arguments of type Token */
  private def expected(token: Token, more: Token*): Nothing =
    val expectedTokens = more.prepended(token).mkString(" or ")
    throw new UnexpectedTokenException(s"Expected: $expectedTokens, found: $curToken")

  /** the root method of the parser: parses an entry phrase */
  // TODO - Part 2 Step 4
  def parsePhrases(): ExprTree =
    if curToken == BONJOUR then readToken()
    if curToken == JE then
      readToken()
      curToken match {
        case ETRE => parseToBeOrNotToBe
        case VOULOIR =>
          readToken()
          curToken match {
            case COMMANDER => parseOrder
            case CONNAITRE => parseSolde
            case _ => expected(COMMANDER, CONNAITRE)
          }
        case ME => parsePseudoByCall
        case _ => expected(ETRE, VOULOIR, ME)
      }
    else
      parsePriceDemand()

  /**
    * Parses the user state of mind or it's pseudo.
    */
  private def parseToBeOrNotToBe = {
    readToken()
    curToken match {
      case ASSOIFFE =>
        readToken()
        Thirsty()
      case AFFAME =>
        readToken()
        Hungry()
      case PSEUDO => Login(eat(PSEUDO).replace("_", ""))
      case _ => expected(ASSOIFFE, AFFAME, PSEUDO)
    }
  }

  /**
    * Parses a balance account request.
    */
  private def parseSolde = {
    readToken()
    eat(MON)
    eat(SOLDE)
    Solde()
  }

  /**
    * Parses a products order.
    */
  private def parseOrder = {
    readToken()
    Order(parseMultiProductRequest(parseProductRequest()))
  }

  /**
    * Parse a product request to generate either a ProductRequest node or a DefaultProductRequest node.
    */
  private def parseProductRequest(): ExprTree =
    //    var brand : String = "" // et un seul Noeud ProductRequest(nb, prod, brand)
    val quantity = Integer.parseInt(eat(NUM))
    val productType = eat(PRODUCT)

    if curToken == MARQUE then
      val brand = eat(MARQUE)
      ProductRequest(quantity, productType, brand)
    else
      DefaultProductRequest(quantity, productType)

  end parseProductRequest

  /**
    * Check if a ProductRequest is followed by more ProductRequest.
    * If so it parses and processes the following requests, else it returns the ProductRequest.
    * @param req a ProductRequest
    */
  @tailrec
  private def parseMultiProductRequest(req: ExprTree): ExprTree = curToken match {
      case ET =>
        readToken()
        parseMultiProductRequest(And(req, parseProductRequest()))
      case OU =>
        readToken()
        parseMultiProductRequest(Or(req, parseProductRequest()))
      case _ => req
    }
  end parseMultiProductRequest

  /**
    * Parses user identification by pseudo.
    */
  private def parsePseudoByCall = {
    readToken()
    eat(APPELLE)
    Login(eat(PSEUDO).replace("_", ""))
  }

  /**
    * Parses product(s) price demand and return a Price node.
    */
  private def parsePriceDemand(): ExprTree =
    if curToken == COMBIEN then
      readToken()
      eat(PRIX)
    else if curToken == QUEL then
      readToken()
      eat(ETRE)
      eat(LE)
      eat(PRIX)
      eat(DE)
    else expected(COMBIEN, QUEL, JE)

    Price(parseMultiProductRequest(parseProductRequest()))

  end parsePriceDemand
