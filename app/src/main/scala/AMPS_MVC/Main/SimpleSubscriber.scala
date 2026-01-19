
package AMPS_MVC.Main

import AMPS_MVC.Json.{JsonToObject, Jsonserialize, TradeEnrichment, TradeRepo}
import AMPS_MVC.Model.{Products, Trade}
import akka.actor.ActorSystem
import com.crankuptheamps.client._
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsError, JsSuccess}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object SimpleSubscriber {

  @throws[Exception]
  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("subscriber-system")
    implicit val executionContext: ExecutionContext = system.dispatcher

    val logger = LoggerFactory.getLogger(getClass)

    val productList: List[Products] = TradeRepo.loadProducts()
    println(s"Loaded ${productList.size} products into cache")
    system.scheduler.scheduleAtFixedRate(
      initialDelay = 0.seconds,
      interval = 5.seconds
    ) { () =>
      TradeRepo.loadProducts()
      println("Product cache refreshed")

      import java.time.LocalDateTime

      val currentTime = LocalDateTime.now()
      println(s"Time : ${currentTime}")
    }

    val client = new Client("SimpleSub")
    client.connect("tcp://192.168.20.60:9007/amps/json")
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

              val enrichment = TradeEnrichment.enrichData(trade, productList)
              TradeEnrichment.isEnriched(trade)

              val jsonpayload = Jsonserialize.tradeToJson(enrichment)

              system.scheduler.scheduleOnce(1.seconds) {
                Publisher.publishing("trades.enriched", jsonpayload)
                println("published using scheduler")
                println("---------------------------------------")
              }
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
