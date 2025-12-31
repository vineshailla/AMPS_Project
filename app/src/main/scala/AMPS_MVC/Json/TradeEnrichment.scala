package AMPS_MVC.Json

import AMPS_MVC.Model.Trade
import AMPS_MVC.Model.Products

object TradeEnrichment {
  def enrichData(tradeData:Trade,product: List[Products]):Trade={
    val matchingProduct = product.find(_.product_name == tradeData.symbol)
    matchingProduct match {
      case Some(product) =>
        val enrichedPrice = if(tradeData.price==0) product.price else tradeData.price
        val enrichedBrokerId = if (tradeData.broker_id.isEmpty) product.broker_id.toString else tradeData.broker_id
        val enrichedCommission = if (tradeData.commission == 0) product.commission else tradeData.commission
        val enrichedTax = if (tradeData.tax == 0) product.tax else tradeData.tax
        val enrichedGrossAmount = enrichedPrice * tradeData.quantity
        val enrichedNetAmount = enrichedGrossAmount - enrichedCommission - enrichedTax
        tradeData.copy(
          price = enrichedPrice,
          broker_id = enrichedBrokerId,
          commission = enrichedCommission,
          tax = enrichedTax,
          gross_amount = enrichedGrossAmount,
          net_amount = enrichedNetAmount
        )
      case None =>
        tradeData
    }
  }
}
