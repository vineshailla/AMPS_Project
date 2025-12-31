
package AMPS_MVC.Main

import AMPS_MVC.Json.{JsonToObject, Jsonserialize, TradeEnrichment, TradeRepo}
import AMPS_MVC.Model.{Products, Trade}
import com.crankuptheamps.client._
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsError, JsSuccess}

object SimpleSubscriber {
  @throws[Exception]
  def main(args: Array[String]): Unit = {

    val logger = LoggerFactory.getLogger(getClass)
    val productList: List[Products] = TradeRepo.loadProducts()
    println(s"Loaded ${productList.size} products into cache")
    val client = new Client("SimpleSub")
    client.connect("tcp://192.168.20.111:9007/amps/json")
    client.logon()
    println("Connected to AMPS")
    val handler = new MessageHandler() {
      override def invoke(msg: Message): Unit = {
        val jsonData = msg.getData
        println(jsonData)
        val deserialized = JsonToObject.tradeDeserializer(jsonData)
       deserialized match {
          case JsSuccess(trades, _) =>
            trades.foreach { trade =>
              println("ORIGINAL TRADE:")
              println(trade)
              val enrichment = TradeEnrichment.enrichData(trade,productList)
              val jsonpayload = Jsonserialize.tradeToJson(enrichment)
             Publisher.publishing("trades.raw",jsonpayload)
              println("published")
            }
          case JsError(errors) =>
            println(s"JSON Parse Error: $errors")
            println(s"Raw Message: $jsonData")

       }
      }
    }
    val cmd = new Command("subscribe").setTopic("trades.raw")
    client.executeAsync(cmd, handler)
    logger.info("Subscribed. Waiting for messages...")
  }
}
