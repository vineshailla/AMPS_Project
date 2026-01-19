
package org.example.Bdd

import AMPS_MVC.Json.TradeEnrichment
import io.cucumber.scala.{EN, ScalaDsl, Scenario}
import org.scalatest.matchers.should.Matchers
import AMPS_MVC.Model.{Products, Trade}

import scala.jdk.CollectionConverters._



class TradeEnrichmentSteps extends ScalaDsl with EN with Matchers {

  private var products: List[Products] = _
  private var inputTrade: Trade = _
  private var enrichedTrade: Trade = _
  private var resultTrade: Trade = _

  Given("the following products exist in the system:") { (dataTable: io.cucumber.datatable.DataTable) =>
    val productMaps = dataTable.asMaps().asScala
    products = productMaps.map { row =>
      Products(
        product_name = row.get("product_name"),
        price = row.get("price").toDouble,
        broker_id = row.get("broker_id"),
        commission = row.get("commission").toFloat,
        tax = row.get("tax").toFloat
      )
    }.toList
  }

  Given("""^a trade with symbol "([^"]*)" and price (\d+)$""") { (symbol: String, price: Int) =>
    inputTrade = createBaseTrade(symbol = symbol, price = price.toDouble)
  }

  Given("""^a trade with symbol "([^"]*)" and price (\d+\.\d+)$""") { (symbol: String, price: Double) =>
    inputTrade = createBaseTrade(symbol = symbol, price = price)
  }

  Given("""^a trade with symbol "([^"]*)" and empty broker_id$""") { (symbol: String) =>
    inputTrade = createBaseTrade(symbol = symbol, broker_id = "")
  }
  Given("""^a trade with symbol "([^"]*)" and broker_id "([^"]*)"$""") { (symbol: String, brokerId: String) =>
    inputTrade = createBaseTrade(symbol = symbol, broker_id = brokerId)
  }
  Given("""^a trade with symbol "([^"]*)" which has no product definition$""") { (symbol: String) =>
    inputTrade = createBaseTrade(symbol = symbol)
  }
  Given("""^a trade with symbol "([^"]*)", quantity (\d+), and price (\d+)$""") { (symbol: String, quantity: Int, price: Int) =>
    inputTrade = createBaseTrade(
      symbol = symbol,
      quantity = quantity,
      price = price.toDouble
    )
  }
  Given("""^any trade$""") { () =>
    inputTrade = createBaseTrade()
  }
  When("""^the enrichment process runs$""") { () =>
    enrichedTrade = TradeEnrichment.enrichData(inputTrade, products)
  }
  When("""^the isEnriched function is called$""") { () =>
    resultTrade = TradeEnrichment.isEnriched(inputTrade)
  }
  Then("""^the trade should be enriched with product price (\d+\.\d+)$""") { (expectedPrice: Double) =>
    enrichedTrade.price shouldBe expectedPrice
  }
  Then("""^the broker_id should be "([^"]*)"$""") { (expectedBrokerId: String) =>
    enrichedTrade.broker_id shouldBe expectedBrokerId
  }

  Then("""^the commission should be (\d+\.\d+)$""") { (expectedCommission: Float) =>
    enrichedTrade.commission shouldBe expectedCommission
  }
  Then("""^the tax should be (\d+\.\d+)$""") { (expectedTax: Float) =>
    enrichedTrade.tax shouldBe expectedTax
  }
  Then("""^the status should be "([^"]*)"$""") { (expectedStatus: String) =>
    enrichedTrade.status shouldBe expectedStatus
  }
  Then("""^gross_amount should be calculated correctly$""") { () =>
    val expectedGross = enrichedTrade.price * enrichedTrade.quantity
    enrichedTrade.gross_amount shouldBe expectedGross
  }
  Then("""^net_amount should be calculated correctly$""") { () =>
    val expectedNet = enrichedTrade.gross_amount - enrichedTrade.commission - enrichedTrade.tax
    enrichedTrade.net_amount shouldBe expectedNet
  }
  Then("""^the trade price should remain (\d+\.\d+)$""") { (expectedPrice: Double) =>
    enrichedTrade.price shouldBe expectedPrice
  }
  Then("""^the trade should remain unchanged$""") { () =>
    enrichedTrade shouldBe inputTrade
  }
  Then("""^the status should not be "([^"]*)"$""") { (status: String) =>
    enrichedTrade.status should not be status
  }
  Then("""^the broker_id should remain "([^"]*)"$""") { (expectedBrokerId: String) =>
    enrichedTrade.broker_id shouldBe expectedBrokerId
  }
  Then("""^the trade should be enriched$""") { () =>
    enrichedTrade.status shouldBe "ENRICHED"
  }
  Then("""^the trade should be marked as "([^"]*)"$""") { (expectedStatus: String) =>
    enrichedTrade.status shouldBe expectedStatus
  }
  Then("""^gross_amount should be (\d+\.\d+)$""") { (expectedGross: Double) =>
    enrichedTrade.gross_amount shouldBe expectedGross
  }
  Then("""^net_amount should be (\d+\.\d+)$""") { (expectedNet: Double) =>
    enrichedTrade.net_amount shouldBe expectedNet
  }
  Then("""^the trade status should be "([^"]*)"$""") { (expectedStatus: String) =>
    resultTrade.status shouldBe expectedStatus
  }
  Then("""^all other fields should remain unchanged$""") { () =>
    val resultWithoutStatus = resultTrade.copy(status = inputTrade.status)
    resultWithoutStatus shouldBe inputTrade
  }

  private def createBaseTrade(
                               trade_id: Int = 1,
                               order_id: String = "ORD001",
                               execution_id: String = "EXEC001",
                               symbol: String = "AAPL",
                               side: String = "BUY",
                               quantity: Int = 10,
                               price: Double = 0.0,
                               trade_time: String = "2024-01-01T10:00:00",
                               venue: String = "NYSE",
                               currency: String = "USD",
                               account_id: String = "ACC001",
                               broker_id: String = "",
                               commission: Float = 0.0f,
                               tax: Float = 0.0f,
                               gross_amount: Double = 0.0,
                               net_amount: Double = 0.0,
                               received_time: String = "2024-01-01T10:00:01",
                               status: String = "RECEIVED"
                             ): Trade = {
    Trade(
      trade_id, order_id, execution_id, symbol, side, quantity, price,
      trade_time, venue, currency, account_id, broker_id, commission, tax,
      gross_amount, net_amount, received_time, status
    )
  }
}