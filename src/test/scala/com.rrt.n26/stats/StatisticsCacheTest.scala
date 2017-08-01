package com.rrt.n26.stats

import java.time.Instant

import com.rrt.n26.objects.{StatisticsResponse, Transaction}
import com.rrt.n26.traits.{InstantTestTrait, TransactionTestTrait}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class StatisticsCacheTest extends FlatSpec
  with Matchers
  with BeforeAndAfterEach
  with InstantTestTrait
  with TransactionTestTrait {

  var statCache: StatisticsCache = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    statCache = new StatisticsCache
    now = Instant.now
  }

  "addTransaction" should "add the transaction to the cache's internal list" in {
    val t = new Transaction()
    statCache.addTransaction(t)

    statCache.getTransactions should contain(t)
  }

  "computestatCache" should "return a default value if there are no transactions" in {
    statCache.computeStats(now) should equal(new StatisticsResponse)
  }

  it should "compute values accurately if there are valid transactions" in {
    val validTime: Instant = makeInstantWithSecondsOffset(-30)
    statCache.addTransaction(makeTransaction(validTime, 30))
    statCache.addTransaction(makeTransaction(validTime, 40))
    statCache.addTransaction(makeTransaction(validTime, 50))

    val expectedResponse = new StatisticsResponse(120d, 40d, 50d, 30d, 3L)

    statCache.computeStats(now) shouldBe expectedResponse
  }

  it should "ignore amounts that don't exist" in {
    val validTime: Instant = makeInstantWithSecondsOffset(-30)
    statCache.addTransaction(makeTransaction(validTime, 30))
    statCache.addTransaction(makeTransaction(validTime, 40))
    statCache.addTransaction(makeTransaction(validTime, 50))
    val badTransition = new Transaction()
    badTransition.setTimestamp(validTime.toEpochMilli)
    statCache.addTransaction(badTransition)

    val expectedResponse = new StatisticsResponse(120d, 40d, 50d, 30d, 3L)

    statCache.computeStats(now) shouldBe expectedResponse
  }

  //currently this just tests that times are no earlier than 60 seconds from "now"
  //todo: update based on clarification on future times
  it should "ignore amounts with invalid timestamps" in {
    val validTime: Instant = makeInstantWithSecondsOffset(-30)
    statCache.addTransaction(makeTransaction(validTime, 30))
    statCache.addTransaction(makeTransaction(validTime, 40))
    statCache.addTransaction(makeTransaction(validTime, 50))

    val invalidTime: Instant = makeInstantWithSecondsOffset(-100)
    statCache.addTransaction(makeTransaction(invalidTime, 30))
    statCache.addTransaction(makeTransaction(invalidTime, 40))
    statCache.addTransaction(makeTransaction(invalidTime, 50))

    val expectedResponse = new StatisticsResponse(120d, 40d, 50d, 30d, 3L)

    statCache.computeStats(now) shouldBe expectedResponse
  }

  "clear" should "remove all transactions" in {
    statCache.addTransaction(new Transaction)
    statCache.addTransaction(new Transaction)
    statCache.addTransaction(new Transaction)

    statCache.getTransactions.size shouldBe 3

    statCache.clear()

    statCache.getTransactions.size shouldBe 0
  }

}
