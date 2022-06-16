// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 24.05.22

package Services

import scala.concurrent.Future
import Utils.FutureOps.randomSchedule

import scala.concurrent.duration.{Duration, DurationConversions, MILLISECONDS}

trait ProductService:
  type BrandName = String
  type ProductName = String

  /**
    * Retrieve the price of a given product.
    * @param product Product's name.
    * @param brand Product's brand name.
    */
  def getPrice(product: ProductName, brand: BrandName): Double

  /**
    * Retrieve the default brand of a given product.
    * @param product Product's name.
    */
  def getDefaultBrand(product: ProductName): BrandName

  def getPreparationTime(product: ProductName): Future[Unit]

class ProductImpl extends ProductService:
  private val Biere = "biere"
  private val Croissant = "croissant"

  private val beers : Map[String, Double] = Map(
    ("boxer", 1.0),
    ("farmer", 1.0),
    ("wittekop", 2.0),
    ("punkipa", 3.0),
    ("jackhammer", 3.0),
    ("tenebreuse", 4.0))

  private val croissants : Map[String, Double] = Map(
    ("maison", 2.0),
    ("cailler", 2.0))

  private val productPrepTime: Map[String, Future[Unit]] = Map(
    (Biere, randomSchedule(Duration(5, MILLISECONDS))),
    (Croissant, randomSchedule(Duration(10, MILLISECONDS))))

  def getPrice(product: ProductName, brand: String): Double =
    if (product == Biere) beers(brand)
    else if (product == Croissant) croissants(brand)
    else 0.0

  def getDefaultBrand(product: ProductName): BrandName =
    if (product == Biere) "boxer"
    else if (product == Croissant) "maison"
    else throw Exception("unknown product")

  def getPreparationTime(product: ProductName): Future[Unit] =
    productPrepTime(product)

end ProductImpl
