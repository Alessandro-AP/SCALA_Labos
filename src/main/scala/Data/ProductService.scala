package Data

trait ProductService:
  type BrandName = String
  type ProductName = String

  def getPrice(product: ProductName, brand: BrandName): Double
  def getDefaultBrand(product: ProductName): BrandName

class ProductImpl extends ProductService:
  // TODO - Part 2 Step 2
  val beers = Map(
    ("boxer", 1.0),
    ("farmer", 1.0),
    ("wittekop", 2.0),
    ("punkipa", 3.0),
    ("jackhammer", 3.0),
    ("tenebreuse", 4.0))

  val croissants = Map(
    ("maison", 2.0),
    ("cailler", 2.0))

  def getPrice(product: ProductName, brand: String): Double =
    if (product == "biere") beers(brand)
    else if (product == "croissant") croissants(brand)
    else 0.0

  def getDefaultBrand(product: ProductName): BrandName =
    if (product == "biere") "maison"
    else if (product == "croissant") "boxer"
    else ""
end ProductImpl
