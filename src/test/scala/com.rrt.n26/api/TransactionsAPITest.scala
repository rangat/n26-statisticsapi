package com.rrt.n26.api

import java.net.URI
import java.time.Instant
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.UriInfo

import com.rrt.n26.StatisticsServer
import com.rrt.n26.objects.Transaction
import com.rrt.n26.stats.StatisticsCache
import com.rrt.n26.traits.{InstantTestTrait, TransactionTestTrait}
import org.mockito.Mockito
import org.mockito.internal.stubbing.answers.Returns
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class TransactionsAPITest extends FlatSpec
  with Matchers
  with BeforeAndAfterEach
  with MockitoSugar
  with InstantTestTrait
  with TransactionTestTrait {

  val stats: StatisticsCache = StatisticsServer.stats
  var api: TransactionsAPI = _

  //mock out the response uri handling
  var uriInfo: UriInfo = mock[UriInfo]
  Mockito.when(uriInfo.getAbsolutePath).then(new Returns(new URI("localhost:8080/transactions")))

  override def beforeEach(): Unit = {
    super.beforeEach()
    stats.clear()
    api = new TransactionsAPI
    now = Instant.now
  }

  "handlePost" should "return 204 response for transaction more than 60s old" in {
    val hundredSecondsAgo: Instant = makeInstantWithSecondsOffset(-100)
    val response = api.handlePost(uriInfo, makeTransaction(hundredSecondsAgo))

    response.getStatusInfo shouldBe Status.NO_CONTENT
  }

  it should "return a 201 response for a transaction under 60s old" in {
    val thirtySecondsAgo: Instant = makeInstantWithSecondsOffset(-30)
    val response = api.handlePost(uriInfo, makeTransaction(thirtySecondsAgo))

    response.getStatusInfo shouldBe Status.CREATED
  }

  it should "return an empty response" in {
    val thirtySecondsAgo: Instant = makeInstantWithSecondsOffset(-30)
    val response = api.handlePost(uriInfo, makeTransaction(thirtySecondsAgo))

    response.bufferEntity() shouldBe false
  }

  it should "add transaction values to StatisticsCache" in {
    val thirtySecondsAgo: Instant = makeInstantWithSecondsOffset(-30)
    val transaction: Transaction = makeTransaction(thirtySecondsAgo, 3.0d)
    val response = api.handlePost(uriInfo, transaction)

    stats.getTransactions should contain(transaction)
  }

  it should "not add transaction values that are rejected" in {
    val hundredSecondsAgo: Instant = makeInstantWithSecondsOffset(-100)
    val transaction: Transaction = makeTransaction(hundredSecondsAgo, 3.0d)
    val response = api.handlePost(uriInfo, makeTransaction(hundredSecondsAgo))


    stats.getTransactions shouldNot contain(transaction)
  }

  //TODO: make sure this is stil true
  it should "accept transactions in the future" in {
    val thirtySecondsInTheFuture: Instant = makeInstantWithSecondsOffset(30)

    val response = api.handlePost(uriInfo, makeTransaction(thirtySecondsInTheFuture))

    response.getStatusInfo shouldBe Status.CREATED
  }


}
