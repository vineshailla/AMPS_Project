package AMPS_MVC.Model

case class Products(
                        product_name: String,
                        price: Double,
                        broker_id: String,
                        commission: Float,
                        tax: Float
                      )