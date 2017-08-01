package com.rrt.n26.traits

import com.rrt.n26.objects.{StatisticsResponse, Transaction}
import com.rrt.n26.stats.StatisticsCache
import org.scalatest.mockito.MockitoSugar

trait MockStatisticsCacheTestTrait extends MockitoSugar {
  val cache = mock[StatisticsCache]
}

class MockStatisticsCache extends StatisticsCache {
  override def shutdownScheduler(): Unit = shutdownFn()

  override def addTransaction(t: Transaction): Unit = addFn(t)

  override def getCachedStats: StatisticsResponse = cachedStatsFn()

  var shutdownFn: (() => Unit) = () => Unit

  var addFn: (Transaction => Unit) = (t) => Unit

  var cachedStatsFn: (() => StatisticsResponse) = () => new StatisticsResponse

}
