// SCALA - Labo 2
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 16.04.22

package Chat

/**
  * This sealed trait represents a node of the tree.
  */
sealed trait ExprTree

/**
  * Declarations of the nodes' types.
  */
object ExprTree:
  // Example cases
  case class Thirsty() extends ExprTree
  case class Hungry() extends ExprTree

  case class Login(name: String) extends ExprTree

  case class ProductRequest(quantity: Int, productType: String, brand: Option[String]) extends ExprTree
  case class Order(request: ExprTree) extends ExprTree
  case class Price(request: ExprTree) extends ExprTree
  case class Balance() extends ExprTree

  case class Or(left: ExprTree, right: ExprTree) extends ExprTree
  case class And(left: ExprTree, right: ExprTree) extends ExprTree
