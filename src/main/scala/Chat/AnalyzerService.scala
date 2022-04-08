package Chat
import Data.{AccountService, ProductService, Session}

class AnalyzerService(productSvc: ProductService,
                      accountSvc: AccountService):
  import ExprTree._
  /**
    * Compute the price of the current node, then returns it. If the node is not a computational node, the method
    * returns 0.0.
    * For example if we had a "+" node, we would add the values of its two children, then return the result.
    * @return the result of the computation
    */
  // TODO - Part 2 Step 3
  def computePrice(t: ExprTree): Double = t match {
    case DefaultProductRequest(nb, productType) =>
      nb * productSvc.getPrice(productType, productSvc.getDefaultBrand(productType))
    case ProductRequest(nb, productType, brand) =>
      nb * productSvc.getPrice(productType, brand)
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
    // you can use this to avoid having to pass the session when doing recursion
    val inner: ExprTree => String = reply(session)
    t match
      // TODO - Part 2 Step 3
      // Example cases
      case Thirsty() => "Eh bien, la chance est de votre côté, car nous offrons les meilleures bières de la région !"
      case Hungry() => "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
      //
      case Login(name) =>
        session.setCurrentUser(name)
        "Hola, " + name + "!"
      case ProductRequest(quantity, productType, brand) => quantity.toString + " " + productType + " " + brand
      case DefaultProductRequest(quantity, productType) => quantity.toString + " " + productType + " " + productSvc.getDefaultBrand(productType)
      case Order(request) =>
        val cost = computePrice(request)
        "Voici donc " + inner(request) + " ! Cela coûte CHF " + cost +
          " et votre nouveau solde est de CHF " + accountSvc.purchase(session.getCurrentUser.get, cost) + "."
      case Price(request) => "Cela coûte CHF " + computePrice(request) + "."
      case Solde() => "Le montant actuel de votre solde est de CHF " + 
        accountSvc.getAccountBalance(session.getCurrentUser.get) + "."
      case Or(left, right) => if computePrice(left) < computePrice(right) then inner(left) else inner(right)
      case And(left, right) => inner(left) + " et " + inner(right)
end AnalyzerService
