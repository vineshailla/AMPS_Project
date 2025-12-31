package AMPS_MVC.Json

import AMPS_MVC.Config.AppConfig
import AMPS_MVC.Model.Products

import javax.security.auth.login.AppConfigurationEntry
import scala.collection.mutable.ListBuffer

object TradeRepo {

  def loadProducts(): List[Products] = {

    val products = ListBuffer[Products]()

    try {
      val query =
        "SELECT product_name, price, broker_id, commission, tax FROM products"

      val appConfig = new AppConfig
      val connection = appConfig.connect()
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(query)

      while (resultSet.next()) {
        val insertProduct = Products(
          product_name = resultSet.getString("product_name"),
          price = resultSet.getDouble("price"),
          broker_id = resultSet.getString("broker_id"),
          commission = resultSet.getFloat("commission"),
          tax = resultSet.getFloat("tax")
        )
        products += insertProduct
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
    products.toList
  }
}