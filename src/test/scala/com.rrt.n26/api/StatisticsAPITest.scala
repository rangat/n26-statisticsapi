package com.rrt.n26.api

import java.time.Instant

import com.rrt.n26.StatisticsServer
import com.rrt.n26.stats.StatisticsCache
import com.rrt.n26.traits.{InstantTestTrait, TransactionTestTrait}
import org.scalatest._

class StatisticsAPITest extends FlatSpec
  with Matchers
  with BeforeAndAfterEach
  with TransactionTestTrait
  with InstantTestTrait {

  val stats: StatisticsCache = StatisticsServer.stats
  var api: StatisticsAPI = _


  override def beforeEach: Unit = {
    super.beforeEach()
    stats.clear()
    api = new StatisticsAPI
    now = Instant.now
  }

  //for more detailed tests on the functionality of StatisticsCache, see StatisticsCacheTest. Fow now, we are just testing that for different inputs of time and variety of Transaction objects in the cache, we get the same result.
  "returnStatistics" should "pass the current time into computeStats" in {
    api.returnStatistics() shouldBe stats.computeStats(api.now)
  }

  it should "return the same as computeStats when there are valid transactions" in {
    val validTime: Instant = makeInstantWithSecondsOffset(-30)
    stats.addTransaction(makeTransaction(validTime, 30))
    stats.addTransaction(makeTransaction(validTime, 40))
    stats.addTransaction(makeTransaction(validTime, 50))

    api.returnStatistics() shouldBe stats.computeStats(api.now)
  }

  it should "return the same as computeStats when there are invalid transactions" in {
    val invalidTime: Instant = makeInstantWithSecondsOffset(-100)
    stats.addTransaction(makeTransaction(invalidTime, 30))
    stats.addTransaction(makeTransaction(invalidTime, 40))
    stats.addTransaction(makeTransaction(invalidTime, 50))

    api.returnStatistics() shouldBe stats.computeStats(api.now)
  }

  it should "return the same as computeStats when there are both valid and invalid transactions" in {
    val validTime: Instant = makeInstantWithSecondsOffset(-30)
    stats.addTransaction(makeTransaction(validTime, 30))
    stats.addTransaction(makeTransaction(validTime, 40))
    stats.addTransaction(makeTransaction(validTime, 50))

    val invalidTime: Instant = makeInstantWithSecondsOffset(-100)
    stats.addTransaction(makeTransaction(invalidTime, 30))
    stats.addTransaction(makeTransaction(invalidTime, 40))
    stats.addTransaction(makeTransaction(invalidTime, 50))

    api.returnStatistics() shouldBe stats.computeStats(api.now)
  }

}
