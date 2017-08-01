package com.rrt.n26.util;

import java.time.Duration;
import java.time.Instant;

public class TimeUtil {

    //The following two utilities take two `Instant` classes as inputs for testability
    public static boolean isTimeOlderThan60Seconds(Instant time, Instant now) {
        return Duration.between(time, now).getSeconds() > 60;
    }
    
    public static boolean isTimeValidForStats(Instant time, Instant now) {
        //This might be a little confusing, so just for clarity:
        //returns true if the time is both in the past and within 60 seconds of now
        return !(TimeUtil.isTimeInTheFuture(time, now) || TimeUtil.isTimeOlderThan60Seconds(time, now));
    }

    public static boolean isTimeInTheFuture(Instant time, Instant now) {
        return time.getEpochSecond() > now.getEpochSecond();
    }

}
