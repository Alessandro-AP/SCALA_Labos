// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 09.05.22

package Data

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

class ProductImpl extends ProductService:
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

  def getPrice(product: ProductName, brand: String): Double =
    if (product == "biere") beers(brand)
    else if (product == "croissant") croissants(brand)
    else 0.0

  def getDefaultBrand(product: ProductName): BrandName =
    if (product == "biere") "boxer"
    else if (product == "croissant") "maison"
    else ""

end ProductImpl
