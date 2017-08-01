package com.rrt.n26.stats

import java.net.URI
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.ws.rs.core.UriInfo

import com.rrt.n26.api.{StatisticsAPI, TransactionsAPI}
import com.rrt.n26.objects.StatisticsResponse
import com.rrt.n26.traits.{InstantTestTrait, TransactionTestTrait}
import org.mockito.Mockito.when
import org.mockito.internal.stubbing.answers.Returns
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

/*
  This will function as a sort-of integration test, testing the round-trip between adding transactions, getting
  statistics, and seeing whether the stats are updated after waiting for some time.
  Unfortunately, these tests  introduce some Non-determinism, so I have tried to introduce sufficient
  delays in each one to allow the scheduler to run.
 I used them to validate my code while running in IntelliJ
  and have shut them off for the automated test suite

 */

class StatisticsCacheSchedulerTest extends FlatSpec
  with Matchers
  with BeforeAndAfterEach
  with MockitoSugar
  with TransactionTestTrait
  with InstantTestTrait {

  var transactions: TransactionsAPI = _
  var statistics: StatisticsAPI = _

  //mock out the response uri handling
  var uriInfo: UriInfo = mock[UriInfo]
  when(uriInfo.getAbsolutePath).then(new Returns(new URI("localhost:8080/transactions")))

  override def beforeEach(): Unit = {
    super.beforeEach()
    transactions = new TransactionsAPI
    statistics = new StatisticsAPI
    now = Instant.now
  }

  "The statistics endpoint" should "return a default value if there are no transactions" ignore {
    TimeUnit.SECONDS.sleep(2)
    statistics.returnStatistics should equal(new StatisticsResponse)
  }


  it should "return stats for a series of valid transactions" ignore {
    transactions.handlePost(uriInfo, makeTransaction(now, 30d))
    transactions.handlePost(uriInfo, makeTransaction(now, 40d))
    transactions.handlePost(uriInfo, makeTransaction(now, 50d))

    TimeUnit.SECONDS.sleep(2)

    statistics.returnStatistics shouldBe new StatisticsResponse(120d, 40d, 50d, 30d, 3L)
  }

  it should "return default stats if the validity time has passed" ignore {
    transactions.handlePost(uriInfo, makeTransaction(now, 30d))
    transactions.handlePost(uriInfo, makeTransaction(now, 40d))
    transactions.handlePost(uriInfo, makeTransaction(now, 50d))

    TimeUnit.SECONDS.sleep(70)

    statistics.returnStatistics shouldBe new StatisticsResponse()
  }

  it should "remove transactions from stats as they become invalid" ignore {
    val validTime10: Instant = makeInstantWithSecondsOffset(-10)
    val validTime20: Instant = makeInstantWithSecondsOffset(-20)
    val validTime30: Instant = makeInstantWithSecondsOffset(-30)

    transactions.handlePost(uriInfo, makeTransaction(validTime10, 30d))
    transactions.handlePost(uriInfo, makeTransaction(validTime20, 40d))
    transactions.handlePost(uriInfo, makeTransaction(validTime30, 50d))

    TimeUnit.SECONDS.sleep(2)

    statistics.returnStatistics shouldBe new StatisticsResponse(120d, 40d, 50d, 30d, 3L)

    TimeUnit.SECONDS.sleep(35)

    statistics.returnStatistics shouldBe new StatisticsResponse(70d, 35d, 40d, 30d, 2L)

    TimeUnit.SECONDS.sleep(10)

    statistics.returnStatistics shouldBe new StatisticsResponse(30d, 30d, 30d, 30d, 1L)

    TimeUnit.SECONDS.sleep(10)

    statistics.returnStatistics shouldBe new StatisticsResponse()
  }

  it should "not account for any invalid transactions in its stats" ignore {
    val ago30: Instant = makeInstantWithSecondsOffset(-30)
    val ago100: Instant = makeInstantWithSecondsOffset(-100)
    val future20: Instant = makeInstantWithSecondsOffset(20)

    transactions.handlePost(uriInfo, makeTransaction(ago30, 30d))
    transactions.handlePost(uriInfo, makeTransaction(ago30, 40d))
    transactions.handlePost(uriInfo, makeTransaction(ago30, 50d))

    transactions.handlePost(uriInfo, makeTransaction(ago100, 30d))
    transactions.handlePost(uriInfo, makeTransaction(ago100, 40d))
    transactions.handlePost(uriInfo, makeTransaction(ago100, 50d))

    transactions.handlePost(uriInfo, makeTransaction(future20, 30d))
    transactions.handlePost(uriInfo, makeTransaction(future20, 40d))
    transactions.handlePost(uriInfo, makeTransaction(future20, 50d))

    TimeUnit.SECONDS.sleep(2)
    
    statistics.returnStatistics shouldBe new StatisticsResponse(120d, 40d, 50d, 30d, 3L)
  }


  

}
