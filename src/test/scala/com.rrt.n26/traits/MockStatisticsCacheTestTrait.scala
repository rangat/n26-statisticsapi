package com.rrt.n26.traits

import com.rrt.n26.objects.{StatisticsResponse, Transaction}
import com.rrt.n26.stats.StatisticsCache
import org.scalatest.mockito.MockitoSugar

trait MockStatisticsCacheTestTrait extends MockitoSugar {
  val cache: StatisticsCache = mock[StatisticsCache]
}

class MockStatisticsCache extends StatisticsCache {
  //This is organized such that one could potentially spoof these functions in the future
  override def shutdownScheduler(): Unit = shutdownFn()

  override def addTransaction(t: Transaction): Unit = addFn(t)

  override def getCachedStats: StatisticsResponse = cachedStatsFn()

  var shutdownFn: (() => Unit) = () => Unit

  var addFn: (Transaction => Unit) = (t) => Unit

  var cachedStatsFn: (() => StatisticsResponse) = () => new StatisticsResponse

}
