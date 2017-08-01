package com.rrt.n26.api

import java.net.URI
import java.time.Instant
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.UriInfo

import com.rrt.n26.objects.Transaction
import com.rrt.n26.stats.StatisticsCacheFactory
import com.rrt.n26.traits.{InstantTestTrait, MockStatisticsCacheTestTrait, TransactionTestTrait}
import org.mockito.Mockito._
import org.mockito.internal.stubbing.answers.Returns
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

class TransactionsAPITest extends FlatSpec
  with Matchers
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with MockitoSugar
  with InstantTestTrait
  with TransactionTestTrait
  with MockStatisticsCacheTestTrait {

  var api: TransactionsAPI = _

  //mock out the response uri handling
  var uriInfo: UriInfo = mock[UriInfo]
  when(uriInfo.getAbsolutePath).then(new Returns(new URI("localhost:8080/transactions")))

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    StatisticsCacheFactory.setInstance(cache)
    api = new TransactionsAPI
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    now = Instant.now
  }

  "handlePost" should "return a 204 response for transaction more than 60s old" in {
    val hundredSecondsAgo: Instant = makeInstantWithSecondsOffset(-100)
    val response = api.handlePost(uriInfo, makeTransaction(hundredSecondsAgo))

    response.getStatusInfo shouldBe Status.NO_CONTENT
  }
  
  it should "return a 204 response if there is no amount or timestamp" in {
    val thirtySecondsAgo: Instant = makeInstantWithSecondsOffset(-30)
    val t = new Transaction
    t.setTimestamp(thirtySecondsAgo.toEpochMilli)
    val response = api.handlePost(uriInfo, t)

    response.getStatusInfo shouldBe Status.NO_CONTENT

    val t2 = new Transaction
    t.setAmount(0d)
    val response2 = api.handlePost(uriInfo, t2)

    response2.getStatusInfo shouldBe Status.NO_CONTENT
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

  it should "add transaction values to StatisticsCacheImpl" in {
    val thirtySecondsAgo: Instant = makeInstantWithSecondsOffset(-30)
    val transaction: Transaction = makeTransaction(thirtySecondsAgo, 3.0d)

    //cache.addFn = (t) => t shouldBe transaction
    val response = api.handlePost(uriInfo, transaction)

    verify(cache).addTransaction(transaction)
  }

  it should "not add transaction values that are rejected" in {
    val hundredSecondsAgo: Instant = makeInstantWithSecondsOffset(-100)
    val transaction: Transaction = makeTransaction(hundredSecondsAgo, 3.0d)
    val response = api.handlePost(uriInfo, makeTransaction(hundredSecondsAgo))

    verify(cache, never()).addTransaction(transaction)
  }

  it should "accept transactions in the future" in {
    val thirtySecondsInTheFuture: Instant = makeInstantWithSecondsOffset(30)

    val response = api.handlePost(uriInfo, makeTransaction(thirtySecondsInTheFuture))

    response.getStatusInfo shouldBe Status.CREATED
  }


}
