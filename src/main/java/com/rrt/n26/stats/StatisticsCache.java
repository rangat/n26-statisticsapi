package com.rrt.n26.stats;

import com.rrt.n26.objects.Transaction;

import java.util.ArrayList;

public class StatisticsCache {

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public StatisticsCache() {
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }




}
