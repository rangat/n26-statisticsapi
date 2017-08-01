package com.rrt.n26.stats;

import com.rrt.n26.objects.StatisticsResponse;
import com.rrt.n26.objects.Transaction;
import com.rrt.n26.util.TimeUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StatisticsCacheImpl implements StatisticsCache {

    // we only ever want one of these
    private final ScheduledFuture<?> cacheUpdates;

    /*
        These two lists represent the entire of transactions sent to the system. The first represents transactions that
        are not as of yet accounted for in statistics. Once a transaction is accounted for, it is moved to the second
        list, and once it is older than 60 seconds old, it is removed entirely.
     */
    protected final List<Transaction> uncomputedTransactions = new ArrayList<>();
    protected final List<Transaction> representedTransactions = new ArrayList<>();
    protected final StatisticsResponse cachedStats = new StatisticsResponse();


    public StatisticsCacheImpl() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        cacheUpdates = scheduler.scheduleWithFixedDelay(this::updateCache, 1, 2,
                TimeUnit.SECONDS);
    }

    @Override
    public void shutdownScheduler() {
        cacheUpdates.cancel(true);
    }

    @Override
    public synchronized void addTransaction(Transaction t) {
        uncomputedTransactions.add(t);
    }

    // the endpoint called by the Statistics /GET simply returns the cached values
    @Override
    public synchronized StatisticsResponse getCachedStats() {
        return cachedStats;
    }

    private synchronized void updateCache() {
        updateCache(Instant.now());
    }

    // When the cache is updating nothing else can update the transaction list.
    protected synchronized void updateCache(Instant now) {
        // Compute which transactions to newly represent in statistics, and which ones to remove from statistics
        List<Transaction> transactionsToAdd = uncomputedTransactions.stream()
                .filter(t -> TimeUtil.isTimeValidForStats(t.toInstant(), now))
                .collect(Collectors.toList());
        List<Transaction> transactionsToRemove = representedTransactions.stream()
                .filter(t -> !TimeUtil.isTimeValidForStats(t.toInstant(), now))
                .collect(Collectors.toList());

        Double sum = cachedStats.getSum();
        Long count = cachedStats.getCount();

        // We want to compute entirely an entirely new min and max, since we don't know if our current min/max is
        // being removed

        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;

        for (Transaction t: transactionsToRemove) {
            Double a = t.getAmount();
            sum -= a;
            count -= 1;
        }

        for (Transaction t: transactionsToAdd) {
            Double a = t.getAmount();
            sum += a;
            count++;
            if (a > max) {
                max = a;
            }
            if (a < min) {
                min = a;
            }
        }

        // update our new represented and uncomputed transactions,
        if (transactionsToRemove.size() > 0) {
            representedTransactions.removeAll(transactionsToRemove);
        }

        if (transactionsToAdd.size() > 0) {
            uncomputedTransactions.removeAll(transactionsToAdd);
            representedTransactions.addAll(transactionsToAdd);
        }

        //need to test our new min and max against all existing values as well
        for (Transaction t: representedTransactions) {
            Double a = t.getAmount();
            if (a > max) {
                max = a;
            }
            if (a < min) {
                min = a;
            }
        }
        
        // finally, update our cache
        cachedStats.setSum(sum);
        cachedStats.setCount(count);
        if (count <= 0) {
            cachedStats.setAvg(null);
            cachedStats.setMin(null);
            cachedStats.setMax(null);
        } else {
            cachedStats.setAvg(sum / count);
            cachedStats.setMin(min);
            cachedStats.setMax(max);
        }
    }
}
