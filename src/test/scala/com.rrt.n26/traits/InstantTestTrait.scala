package com.rrt.n26.traits

import java.time.Instant

/*
  Used to define an Instant for "Now" in each class and some helper functions
 */
trait InstantTestTrait {

  var now: Instant = _

  def makeInstantWithSecondsOffset(seconds: Int, t: Instant = now): Instant = {
    t.plusSeconds(seconds)
  }

}
