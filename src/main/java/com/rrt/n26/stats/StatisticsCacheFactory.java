package com.rrt.n26.stats;

public class StatisticsCacheFactory {

    private static StatisticsCache cache;

    public static void setInstance(StatisticsCache stats) {
        cache = stats;
    }

    public static StatisticsCache getInstance() {
        if (cache == null) {
            cache = new StatisticsCacheImpl();
        }

        return cache;
    }
}
