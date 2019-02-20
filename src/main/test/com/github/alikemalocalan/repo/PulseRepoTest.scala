package com.github.alikemalocalan.repo

import slick.jdbc.PostgresProfile.api._

class PulseRepoTest extends FunSuite {
  val db: Database = Database.forConfig("slick-postgres")

  test("testFindByToken") {
    //db.run(TableQuery[TokenTable].schema.create)
    //    val result = new Repo(db).insertUser(Pulse("asd"))
    //    println(result)
  }

  test("testUsers") {

  }

  test("testInsertUser") {

  }

}
