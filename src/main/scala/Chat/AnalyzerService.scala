// SCALA - Labo 4
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 19.06.22

package Chat
import Services.{AccountService, ProductService, Session}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import java.util.concurrent.TransferQueue

class AnalyzerService(productSvc: ProductService,
                      accountSvc: AccountService,
                      tq: TransferQueue[String]):
  import ExprTree._

  val askForAuth = "Veuillez d'abord vous identifier."

  /**
    * Compute the price of the current node, then returns it. If the node is not a computational node, the method
    * returns 0.0.
    * For example if we had a "+" node, we would add the values of its two children, then return the result.
    * @return the result of the computation
    */
  def computePrice(t: ExprTree): Double = t match {
    case ProductRequest(quantity, productType, brand) =>
      quantity * productSvc.getPrice(productType, brand.getOrElse(productSvc.getDefaultBrand(productType)))
    case And(left, right) => computePrice(left) + computePrice(right)
    case Or(left, right) => Math.min(computePrice(left), computePrice(right))
    case Price(request) => computePrice(request)
    case Order(request) => computePrice(request)
    case _ => 0.0
  }

  /**
    * Return the output text of the current node, in order to write it in console.
    * @return the output text of the current node
    */
  def reply(session: Session)(t: ExprTree): String =
    // You can use this to avoid having to pass the session when doing recursion
    val inner: ExprTree => String = reply(session)
    t match
      // Example cases
      case Thirsty() => "Eh bien, la chance est de votre côté, car nous offrons les meilleures bières de la région !"
      case Hungry() => "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
      // Identification
      case Login(name) => processLogin(name, session)
      // Orders & products
      case ProductRequest(quantity, productType, brand) => s"${quantity.toString} $productType ${brand.getOrElse(productSvc.getDefaultBrand(productType))}"
      case Order(request) => processOrder(request, session)
      case Price(request) => s"Cela coûte CHF ${computePrice(request)}."
      case Balance() => processSolde(session)
      // Logical op
      case Or(left, right) => if computePrice(left) <= computePrice(right) then inner(left) else inner(right)
      case And(left, right) => inner(left) + " et " + inner(right)

  /**
    * Processes a login request and adds a user to the account service if needed.
    * @param name the user name.
    * @param session the current session.
    * @return user welcome message.
    */
  private def processLogin(name: String, session: Session): String =
    session.setCurrentUser(name)
    if !accountSvc.isAccountExisting(name) then accountSvc.addAccount(name, accountSvc.defaultBalance)
    s"Hola, $name!"

  /**
    * Processes an order request, updating a user account balance.
    * @param request the order request.
    * @param session the current session.
    * @return if the user is logged in, returns the order response, otherwise a login invitation.
    */
  private def processOrder(request: ExprTree, session: Session): String =
    session.getCurrentUser.map(u => {
      val originalCost = computePrice(request)
      prepareOrder(request).transform {
        // Avec notre implémentation, ça n'a pas de sens de renvoyer un futur ici,
        // car on en fait rien. Il faudrait utiliser onComplete (juste besoin du side effect),
        // mais comme nous n'avons pas le droit de l'utiliser, nous avons utilisé transform.
        case Success(products) =>
          val cost = computePrice(products)
            try {
              val creditBalance = accountSvc.purchase(u, cost)
              if cost != originalCost then // partial order
                tq.transfer(s"Voici votre commande partielle : ${reply(session)(products)} ! Cela coûte " +
                  s"CHF $cost et votre nouveau solde est de CHF $creditBalance.")
                Try(())
              else
                tq.transfer(s"Voici donc ${reply(session)(products)} ! Cela coûte CHF $cost et " +
                  s"votre nouveau solde est de CHF $creditBalance.")
                Try(())
            }
            catch{ case e : Exception => 
              tq.transfer(e.getMessage)
              Try(e)
            }
        case Failure(e) =>
          tq.transfer(s"La commande de ${reply(session)(request)} ne peut pas être délivrée")
          Try(e)
      }
      s"Votre commande est en cours de préparation: ${reply(session)(request)}."
    }).getOrElse(askForAuth)

  /**
    * Processes a request for an user account balance.
    * @param session the current session.
    * @return if the user is logged in, returns the user balance, otherwise a login invitation.
    */
  private def processSolde(session: Session): String =
    session.getCurrentUser.map(u => s"Le montant actuel de votre solde est de CHF ${accountSvc.getAccountBalance(u)}.").getOrElse(askForAuth)

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  /**
    * Prepare a future order.
    * @param t  ExprTree of the order
    * @return a future of the order ExprTree after preparation done, or a future of failure if
    *         preparation of the order fail.
    */
  def prepareOrder(t: ExprTree): Future[ExprTree] = t match
      // products
      case ProductRequest(quantity, productType, brand) =>
        productSvc.getPreparationTime(productType, brand.getOrElse(productSvc.getDefaultBrand(productType)))
          .flatMap { _ => Future.successful(ProductRequest(quantity, productType, brand)) }
      // Logical op
      case Or(left, right) => if computePrice(left) <= computePrice(right) then prepareOrder(left) else prepareOrder(right)
      case And(left, right) =>
        val futures = List(prepareOrder(left), prepareOrder(right))
        val futureListOfTry = Future.sequence(futures.map(_.transform(Success(_))))

        futureListOfTry.flatMap { listOfTry =>
          val l = listOfTry.collect { case Success(x) => x }
          l.size match
            case 0 => Future.failed(Exception("Tous les futures de la commande ont échoués"))
            case 1 => Future.successful(l.head)
            case 2 => Future.successful(And(l.head, l.last))
        }
        // alternative avec map :
//        futureListOfTry.map { listOfTry =>
//          val l = listOfTry.collect { case Success(x) => x }
//          l.size match
//            case 0 => throw Exception("Tous les futures de la commande ont échoués")
//            case 1 => l.head
//            case 2 => And(l.head, l.last)
//        }
      case _ => throw Exception("Only orders can be prepared")

end AnalyzerService
