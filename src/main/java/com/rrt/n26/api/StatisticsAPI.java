package com.rrt.n26.api;

import com.rrt.n26.objects.StatisticsResponse;
import com.rrt.n26.stats.StatisticsCache;
import com.rrt.n26.stats.StatisticsCacheFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("statistics")
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsAPI {

    private final StatisticsCache stats = StatisticsCacheFactory.getInstance();
    
    /**
     *
     * Handles HTTP GET Requests.
     *
     * @return JSON-friendly response object representing statistics
     */
    @GET
    public StatisticsResponse returnStatistics() {
        return stats.getCachedStats();
    }
}
