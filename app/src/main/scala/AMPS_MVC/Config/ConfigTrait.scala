package AMPS_MVC.Config

import java.sql.Connection


trait ConfigTrait {
  def dbUrl : String
  def dbUser :String
  def dbPassword : String
  def connect(): Connection
}

