
package AMPS_MVC.Main

import com.crankuptheamps.client._

object Publisher {

  private val client = new Client("EnrichedTradePublisher")
   client.connect("tcp://192.168.20.184:9007/amps/json")
    client.logon()
    println("Publisher connected to AMPS")

  def publishing(topic: String,jsonData: String) = {
    client.publish(topic,jsonData)
  }
  def stop(): Unit = {
    client.close()
    println("Publisher closed")
  }
}

