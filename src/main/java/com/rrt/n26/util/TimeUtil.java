package com.rrt.n26.util;

import java.time.Duration;
import java.time.Instant;

public class TimeUtil {

    //The following two utilities take two `Instant` classes as inputs for testability
    public static boolean isTimeOlderThan60Seconds(Instant time, Instant now) {
        return Duration.between(time, now).getSeconds() > 60;
    }

    public static boolean isTimeInTheFuture(Instant time, Instant now) {
        return time.getEpochSecond() > now.getEpochSecond();
    }
}
