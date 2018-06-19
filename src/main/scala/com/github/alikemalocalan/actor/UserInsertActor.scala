package com.github.alikemalocalan.actor

import akka.actor.{Actor, ActorLogging}
import com.github.alikemalocalan.model.User
import com.github.alikemalocalan.repo.Repo
import slick.jdbc.PostgresProfile.api.Database

class UserInsertActor(db: Database) extends Actor with ActorLogging {
  val userRepo = new Repo(db)

  override def receive: Receive = {
    case User(x, y) =>
      log.info(s"User request: name: $x, id: $y")
      val result = userRepo.insertUser(User(x, y))
      sender() ! result
    case _ => log.error("Not inserted User")
  }

}
