package com.rrt.n26.stats;

import com.rrt.n26.objects.StatisticsResponse;
import com.rrt.n26.objects.Transaction;

public interface StatisticsCache {

    void shutdownScheduler();

    void addTransaction(Transaction t);

    StatisticsResponse getCachedStats();





}
