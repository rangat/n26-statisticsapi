package com.rrt.n26.stats;

import com.rrt.n26.objects.StatisticsResponse;
import com.rrt.n26.objects.Transaction;
import com.rrt.n26.util.TimeUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsCache {

    private List<Transaction> transactions = new ArrayList<>();

    public StatisticsCache() {
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public StatisticsResponse computeStats(Instant now) {
        //Pass #1: Using an inefficient O(n) algorithm to compute statistics

        //The filter ensures no times earlier than 60 seconds before "now" are added to the total
        List<Double> amounts =  transactions.stream()
                                        .filter(t -> !TimeUtil.isTimeOlderThan60Seconds(t.getInstant(), now))
                                        .map(Transaction::getAmount)
                                        .collect(Collectors.toList());

        if (amounts.isEmpty()) {
            return new StatisticsResponse();
        }


        
        //Wanted to use the Java 8 stream min/max/reduce functions here, but found the option handling inelegant
        long count = amounts.size();
        double sum = 0;
        double min = amounts.get(0);
        double max = amounts.get(0);

        for (Double amt: amounts) {
            sum += amt;
            if (amt < min) {
                min = amt;
            }
            if (amt > max) {
                max = amt;
            }

        }

        double avg = sum / count;

        return new StatisticsResponse(sum, avg, max, min, count);

    }




}
