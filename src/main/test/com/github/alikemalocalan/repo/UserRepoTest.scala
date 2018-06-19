package com.github.alikemalocalan.repo

import com.github.alikemalocalan.model.User
import slick.jdbc.PostgresProfile.api._

class UserRepoTest extends FunSuite {
  val db: Database = Database.forConfig("slick-postgres")

  test("testFindByToken") {
    //db.run(TableQuery[TokenTable].schema.create)
    val result = new Repo(db).insertUser(User("asd"))
    println(result)
  }

  test("testUsers") {

  }

  test("testInsertUser") {

  }

}
