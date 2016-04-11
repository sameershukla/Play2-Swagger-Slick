package models.db

import play.api.db.slick.Config.driver.simple._
import models.Consumer

class Consumers(tag: Tag) extends Table[Consumer](tag, "CONSUMERS") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def lastName = column[String]("LastName")
  def gender = column[String]("GENDER")
  def email = column[String]("EMAIL", O.NotNull)
  def password = column[String]("PASSWORD")
  def zip = column[String]("ZIP")
  def phone = column[String]("PHONE")

  def idIndex = index("ID_INDEX", (id), unique = true)
  def idx = index("idx_a", (email, phone), unique = true)
  
  def * = (id.?, name, lastName, gender, email, password, zip, phone) <> ((Consumer.apply _).tupled, Consumer.unapply)
}
