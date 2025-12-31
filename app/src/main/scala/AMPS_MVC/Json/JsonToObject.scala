package AMPS_MVC.Json

import AMPS_MVC.Model.Trade
import play.api.libs.json.{Format, JsError, JsResult, JsSuccess, JsValue, Json, Reads}

object JsonToObject {

  def tradeDeserializer(jsonData: String): JsResult[List[Trade]] = {
    implicit val tradeFormat: Format[Trade] = Json.format[Trade]
    val deserialized = Json.parse(jsonData).validate[List[Trade]]
    deserialized
  }
}