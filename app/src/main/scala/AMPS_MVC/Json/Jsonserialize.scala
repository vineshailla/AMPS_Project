package AMPS_MVC.Json

import AMPS_MVC.Model.Trade

import play.api.libs.json.{Format, Json}

object Jsonserialize {
  implicit val tradeFormat : Format[Trade] = Json.format[Trade]
  def tradeToJson(trade:Trade):String={
    Json.prettyPrint(Json.toJson(trade))
  }
}
