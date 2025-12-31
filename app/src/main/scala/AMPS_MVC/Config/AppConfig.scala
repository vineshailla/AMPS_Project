package AMPS_MVC.Config

import com.typesafe.config.ConfigFactory

import java.sql.{Connection, DriverManager}

class AppConfig extends ConfigTrait {

  private val config = ConfigFactory.load()
  private val dataConfig = config.getConfig("db")

  override def dbUrl: String =
    dataConfig.getString("url")

  override def dbUser: String =
    dataConfig.getString("user")

  override def dbPassword: String =
    dataConfig.getString("password")

  override def connect(): Connection =  DriverManager.getConnection(dbUrl, dbUser, dbPassword)
}