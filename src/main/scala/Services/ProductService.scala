// SCALA - Labo 4
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 19.06.22

package Services

import scala.concurrent.Future
import Utils.FutureOps.randomSchedule

import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.duration._

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

  /**
    * Retrieve the preparation time of a given product.
    * @param product Product's name.
    * @param brand Product's brand name.
    * @return
    */
  def getPreparationTime(product: ProductName, brand: BrandName): Future[Unit]

class ProductImpl extends ProductService:
  private val Biere = "biere"
  private val Croissant = "croissant"

  case class Schedule(mean: Duration, std: Duration = 0.second, successRate: Double = 1.0)
  case class ProductInfo(price: Double, schedule: Schedule)

  private val beers : Map[BrandName, ProductInfo] = Map(
    ("boxer", ProductInfo(1.0, Schedule(1.second, successRate = 0.6))),
    ("farmer", ProductInfo(1.0, Schedule(1.second, successRate = 0.6))),
    ("wittekop", ProductInfo(2.0, Schedule(2.second, successRate = 0.6))),
    ("punkipa", ProductInfo(3.0, Schedule(3.second, successRate = 0.6))),
    ("jackhammer", ProductInfo(3.0, Schedule(3.second, successRate = 0.6))),
    ("tenebreuse", ProductInfo(4.0, Schedule(4.second, successRate = 0.6))))

  private val croissants : Map[BrandName, ProductInfo] = Map(
    ("maison", ProductInfo(2.0, Schedule(1.second, successRate = 0.6))),
    ("cailler", ProductInfo(2.0, Schedule(1.second, successRate = 0.6))))

  def getPrice(product: ProductName, brand: BrandName): Double =
    if (product == Biere) beers(brand).price
    else if (product == Croissant) croissants(brand).price
    else 0.0

  def getDefaultBrand(product: ProductName): BrandName =
    if (product == Biere) "boxer"
    else if (product == Croissant) "maison"
    else throw Exception("unknown product")

  def getPreparationTime(product: ProductName, brand: BrandName): Future[Unit] =
    if (product == Biere)
      val s = beers(brand).schedule
      randomSchedule(s.mean, successRate = s.successRate)
    else if (product == Croissant)
      val s = croissants(brand).schedule
      randomSchedule(s.mean, successRate = s.successRate)
    else randomSchedule(Duration(0, SECONDS))

end ProductImpl
