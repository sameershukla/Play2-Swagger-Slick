package models

import models.db.Consumers
import play.api.Play.current
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._

/**
 * Domain model - for the Consumers
 * ordering on the collection. The position is 1-based and strictly consecutive.
 */
case class Consumer(id: Option[Long], name: String, lastName: String, gender: String, email: String, password: String, zip: String, phone: String)

/**
 * Data access functions.
 */
object Consumer {

  // Base Slick database query to use for data access.
  val consumers = TableQuery[Consumers]

  /**
   * Returns a list of consumers, sorted by id.
   */
  def list = DB.withSession { implicit s: Session =>
    consumers.sortBy(_.id).list
  }

  /**
   * Find Consumer By Id
   */
  def byId(id: Long) = DB.withSession { implicit s: Session =>
    consumers.filter { x => x.id === id }.firstOption
  }

  /**
   * Add Consumer
   */
  def add(consumer: Consumer) = DB.withSession { implicit s: Session =>
    consumers += consumer
  }

  /**
   * Delete Consumer
   */
  def delete(id: Long) = DB.withSession { implicit s: Session =>
    consumers.filter { x => x.id === id }.delete
  }

  /**
   * Update Consumer
   */
  def update(consumer: Consumer) = DB.withSession { implicit s: Session =>
    consumers.filter { x => x.id === consumer.id }.update(consumer)
  }

  /**
   * This block check the uniqueness of record by email id which is Unique
   */
  def insertIfNotExists(email: String) = DB.withSession { implicit s: Session =>
    println("email:"+email)
    
    consumers.filter { x => x.email === email }.firstOption
  }

}