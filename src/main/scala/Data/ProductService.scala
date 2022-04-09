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
  // TODO - Part 2 Step 2
  private val beers = Map(
    ("boxer", 1.0),
    ("farmer", 1.0),
    ("wittekop", 2.0),
    ("punkipa", 3.0),
    ("jackhammer", 3.0),
    ("tenebreuse", 4.0))

  private val croissants = Map(
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
