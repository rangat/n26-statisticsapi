package com.rrt.n26.traits

import java.time.Instant

import com.rrt.n26.objects.Transaction

//Helper functions to make Transaction objects
trait TransactionTestTrait {
  //Helper Functions
  def makeTransaction(timestamp: Instant, amount: Double = 0): Transaction = {
    new Transaction(amount, timestamp.toEpochMilli)
  }
}
