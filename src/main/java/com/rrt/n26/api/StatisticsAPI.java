package com.rrt.n26.api;

import com.rrt.n26.StatisticsServer;
import com.rrt.n26.objects.StatisticsResponse;
import com.rrt.n26.stats.StatisticsCache;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;

@Path("statistics")
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsAPI {

    private final StatisticsCache stats = StatisticsServer.stats;

    protected Instant now = Instant.now();

    /**
     *
     * Handles HTTP GET Requests.
     *
     * @return JSON-friendly response object representing statistics
     */
    @GET
    public StatisticsResponse returnStatistics() {
        now = Instant.now();
        return stats.computeStats(now);
    }
}
