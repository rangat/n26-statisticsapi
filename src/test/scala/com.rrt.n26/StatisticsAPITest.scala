package com.rrt.n26

import javax.ws.rs.client.{Client, ClientBuilder, WebTarget}

import org.glassfish.grizzly.http.server.HttpServer
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

class StatisticsAPITest extends FlatSpec with Matchers with BeforeAndAfterAll {

  private var server: HttpServer = _
  private var target: WebTarget = _

  override def beforeAll: Unit = {
    server = StatisticsServer.startServer()
//    val c: Client = ClientBuilder.newClient().register(Main.moxyJsonResolver())
//    target = c.target(Main.BASE_URI)
  }

  override def afterAll: Unit = {
    server.shutdownNow()
  }

}
