package AMPS_MVC.UserApplication


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}

object FrontendConnecting extends App {

//  println(().isInstanceOf[Nothing])

  implicit val system: ActorSystem = ActorSystem("trade-project")
  implicit val executionContext : ExecutionContext = system.dispatcher

  private val route =
    path("") {
      get {
        val html = try {
          val source = scala.io.Source.fromFile("app/Project/Trades.html")
          try {
            source.mkString
          }finally {
            source.close()
          }
        }
        catch {
          case _ : Exception =>
            """
               <html>
                  <body>
                    <p>HTML file not found</p>
                    <p>Create file - app/project/index.html</p>
                  </body>
               </html>
            """
        }
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)` , html))
      }
    }~
      path("trade") {
        post {
          formFields("symbol", "side", "quantity".as[Int], "currency") {
            (symbol, side, quantity, currency) =>
              // Store in variables
//              val tradeSymbol: String = symbol
//              val tradeSide: String = side
//              val tradeQuantity: Int = quantity
//              val tradeCurrency: String = currency

              // Print to console
              println("=" * 50)
              println(" NEW TRADE RECEIVED:")
              println(s"  Symbol:   $symbol")
              println(s"  Side:     $side")
              println(s"  Quantity: $quantity")
              println(s"  Currency: $currency")
              println("=" * 50)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
                s"""
                   <html>
                      <body style = "padding : 20px ; font-family : Arial;">
                        <h2>TRADE SUBMITTED</h2>
                        <p><strong>Symbol : $symbol</strong></p>
                        <p><strong>Symbol : $side</strong></p>
                        <p><strong>Symbol : $quantity</strong></p>
                        <p><strong>Quantity : $currency</strong></p>
                        <a href = "/">GO BACK</a>
                      </body>
                   </html>
                """
              ))
          }
        }
      }

  private val bindingFuture = Http()
    .newServerAt("localhost" , 9000)
    .bindFlow(route)

  println("Server started at http://localhost:9000")
  println("Press ENTER to end...")
  bindingFuture.onComplete {
    case Success(binding) =>
      val address = binding.localAddress
      println(s"Server started at http://${address.getHostString}:${address.getPort}")
    case Failure(exception) =>
      println(s"Failed to bind HTTP server : ${exception.getMessage}")
    system.terminate()
  }
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete { _ =>
      println("Server stopped")
      system.terminate()
    }
}
