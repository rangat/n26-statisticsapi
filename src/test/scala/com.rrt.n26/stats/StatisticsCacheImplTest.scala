package com.rrt.n26.stats

import java.time.Instant

import com.rrt.n26.objects.{StatisticsResponse, Transaction}
import com.rrt.n26.traits.{InstantTestTrait, TransactionTestTrait}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class StatisticsCacheImplTest extends FlatSpec
  with Matchers
  with BeforeAndAfterEach
  with InstantTestTrait
  with TransactionTestTrait {

  var statCache: StatisticsCacheImpl = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    statCache = new StatisticsCacheImpl
    //none of these tests rely on the scheduler; we will trigger "updateCache" manually
    statCache.shutdownScheduler()
    now = Instant.now
  }

  "addTransaction" should "add the transaction to the cache's uncomputedTransactions" in {
    val t = new Transaction()
    statCache.addTransaction(t)

    statCache.uncomputedTransactions should contain(t)
  }

  "getCachedStats" should "return the internal cachedStats" in {
    statCache.cachedStats.setSum(0d)
    statCache.cachedStats.setMin(0d)
    statCache.cachedStats.setMax(0d)
    statCache.cachedStats.setAvg(0d)
    statCache.cachedStats.setCount(1L)

    statCache.getCachedStats shouldBe new StatisticsResponse(0d, 0d, 0d, 0d, 1L)
  }

  "updateCache" should "return a default value if there are no transactions" in {
    statCache.updateCache(now)
    statCache.cachedStats should equal(new StatisticsResponse)
  }

  it should "update the stats based on all uncomputed transactions" in {
    val validTime30:Instant = makeInstantWithSecondsOffset(-30)
    statCache.addTransaction(makeTransaction(validTime30, 30))
    statCache.addTransaction(makeTransaction(validTime30, 40))
    statCache.addTransaction(makeTransaction(validTime30, 50))

    statCache.updateCache(now)

    statCache.cachedStats shouldBe new StatisticsResponse(120d, 40d, 50d, 30d, 3L)
  }

  it should "ignore any transactions that are invalid" in {
    val validTime30: Instant = makeInstantWithSecondsOffset(-30)
    statCache.addTransaction(makeTransaction(validTime30, 30))
    statCache.addTransaction(makeTransaction(validTime30, 40))
    statCache.addTransaction(makeTransaction(validTime30, 50))

    val invalidTime100: Instant = makeInstantWithSecondsOffset(-100)
    statCache.addTransaction(makeTransaction(invalidTime100, 30))
    statCache.addTransaction(makeTransaction(invalidTime100, 40))
    statCache.addTransaction(makeTransaction(invalidTime100, 50))

    val futureTime: Instant = makeInstantWithSecondsOffset(20)
    statCache.addTransaction(makeTransaction(futureTime, 30))
    statCache.addTransaction(makeTransaction(futureTime, 40))
    statCache.addTransaction(makeTransaction(futureTime, 50))

    statCache.updateCache(now)

    statCache.cachedStats shouldBe new StatisticsResponse(120d, 40d, 50d, 30d, 3L)
  }

  it should "move valid transactions from uncomputed to represented" in {
    val validTime30: Instant = makeInstantWithSecondsOffset(-30)
    val ta = makeTransaction(validTime30, 30)
    statCache.addTransaction(ta)
    val tb = makeTransaction(validTime30, 40)
    statCache.addTransaction(tb)
    val tc = makeTransaction(validTime30, 50)
    statCache.addTransaction(tc)

    statCache.updateCache(now)

    statCache.representedTransactions should contain(ta)
    statCache.representedTransactions should contain(tb)
    statCache.representedTransactions should contain(tc)
  }

  it should "remove represented transactions that are no longer valid" in {
    val invalidTime10: Instant = makeInstantWithSecondsOffset(10)
    val ta = makeTransaction(invalidTime10, 30)
    statCache.addTransaction(ta)
    val tb = makeTransaction(invalidTime10, 40)
    statCache.addTransaction(tb)
    val tc = makeTransaction(invalidTime10, 50)
    statCache.addTransaction(tc)

    statCache.updateCache(now)

    statCache.representedTransactions shouldNot contain(ta)
    statCache.representedTransactions shouldNot contain(tb)
    statCache.representedTransactions shouldNot contain(tc)
  }
  
}
