package AMPS_MVC.Model
import java.time.LocalDateTime

case class Trade(trade_id: Int, order_id: String, execution_id: String, symbol: String, side: String, quantity: Int, price: Double, trade_time: String, venue: String, currency: String, account_id: String, broker_id: String, commission: Float, tax: Float, gross_amount: Double, net_amount: Double, received_time: String, status: String)




