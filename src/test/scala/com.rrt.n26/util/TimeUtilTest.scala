package com.rrt.n26.util

import java.time.Instant

import com.rrt.n26.traits.InstantTestTrait
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class TimeUtilTest extends FlatSpec
  with Matchers
  with InstantTestTrait
  with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    now = Instant.now
  }

  "isTimeOlderThan60Seconds" should "return true if time is over 60 seconds from now" in {
    val t = makeInstantWithSecondsOffset(-90)

    TimeUtil.isTimeOlderThan60Seconds(t, now) shouldBe true
  }

  it should "return false if time is less than 60 seconds from now" in {
    val t = makeInstantWithSecondsOffset(-30)

    TimeUtil.isTimeOlderThan60Seconds(t, now) shouldBe false
  }

  it should "return false if time is newer than now" in {
    val t = makeInstantWithSecondsOffset(30)

    TimeUtil.isTimeOlderThan60Seconds(t, now) shouldBe false
  }

  "isTimeValidForStats" should "return true if time is in the past and within 60 seconds" in {
    val t = makeInstantWithSecondsOffset(-30)

    TimeUtil.isTimeValidForStats(t, now) shouldBe true
  }

  it should "return false if time is further than 60 seconds in the past" in {
    val t = makeInstantWithSecondsOffset(-90)

    TimeUtil.isTimeValidForStats(t, now) shouldBe false
  }

  it should "return false if time is in the future" in {
    val t = makeInstantWithSecondsOffset(300)

    TimeUtil.isTimeValidForStats(t, now) shouldBe false
  }

  "isTimeInTheFuture" should "return true if time is ahead of now" in {
    val t = makeInstantWithSecondsOffset(30)

    TimeUtil.isTimeInTheFuture(t, now) shouldBe true
  }

  it should "return false if time is behind now" in {
    val t = makeInstantWithSecondsOffset(-30)

    TimeUtil.isTimeInTheFuture(t, now) shouldBe false
  }

}
