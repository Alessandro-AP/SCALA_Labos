// SCALA - Labo 4
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 19.06.22

package Services

//import scala.collection.mutable
import scala.collection.concurrent.TrieMap

trait AccountService:
  val defaultBalance: Double = 30.0

  /**
    * Retrieve the balance of a given account
    * @param user the name of the user whose account will be retrieve
    * @return the current balance of the user
    */
  def getAccountBalance(user: String): Double

  /**
    * Add an account to the existing accounts
    * @param user the name of the user
    * @param balance the initial balance value
    */
  def addAccount(user: String, balance: Double): Unit

  /**
    * Indicate is an account exist
    * @param user the name of the user whose account is checked to exist
    * @return whether the account exists or not
    */
  def isAccountExisting(user: String): Boolean

  /**
    * Update an account by decreasing its balance.
    * @param user the name of the user whose account will be updated
    * @param amount the amount to decrease
    * @return the new balance
    */
  def purchase(user: String, amount: Double): Double

class AccountImpl extends AccountService:
  private val accounts: TrieMap[String, Double] = TrieMap()

  def getAccountBalance(user: String): Double = accounts getOrElse (user, 0.0)

  def addAccount(user: String, balance: Double): Unit = accounts += (user -> balance)

  def isAccountExisting(user: String): Boolean = accounts contains user
  
  def purchase(user: String, amount: Double): Double =
    require(isAccountExisting(user), "User unknown!")
    val newBalance = accounts(user) - amount
    accounts.updateWith(user)(_ => Some(newBalance)).getOrElse(accounts(user))

end AccountImpl
