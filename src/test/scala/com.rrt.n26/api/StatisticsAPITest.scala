package com.rrt.n26.api

import com.rrt.n26.stats.StatisticsCacheFactory
import com.rrt.n26.traits.MockStatisticsCacheTestTrait
import org.mockito.Mockito.verify
import org.scalatest._

class StatisticsAPITest extends FlatSpec
  with Matchers
  with BeforeAndAfterAll
  with MockStatisticsCacheTestTrait {

  var api: StatisticsAPI = _
  
  protected override def beforeAll: Unit = {
    super.beforeAll()
    StatisticsCacheFactory.setInstance(cache)
    api = new StatisticsAPI
  }

  // for more detailed tests on the functionality of StatisticsCacheImpl, see StatisticsCacheTest. Fow now, we are just
  // testing that for the variety of Transaction objects in the cache, we get the same result.
  it should "return call StatisticsCache.getCachedStats" in {
    api.returnStatistics()

    verify(cache).getCachedStats
  }

}
