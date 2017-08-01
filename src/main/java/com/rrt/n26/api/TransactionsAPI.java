package com.rrt.n26.api;

import com.rrt.n26.StatisticsServer;
import com.rrt.n26.objects.Transaction;
import com.rrt.n26.stats.StatisticsCache;
import com.rrt.n26.util.TimeUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;

@Path("transactions")
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionsAPI {

    private final StatisticsCache stats = StatisticsServer.stats;

    /**
     * Handle HTTP POST requests.
     *
     * @return Empty HTTP Response 201 if the transaction is validated, or 204 otherwise
     */
    @POST
    public Response handlePost(@Context UriInfo ui, Transaction t) {
        //TODO: wrap this all in trycatch
        Instant now = Instant.now();

        //TODO: Delete this when done integration testing or after writing better tests
        System.out.println("now: " + now.toEpochMilli());
        System.out.println("time: " + t.toInstant().toString());

        if (!TimeUtil.isTimeOlderThan60Seconds(t.toInstant(), now)) {
            stats.addTransaction(t);
            return Response.created(ui.getAbsolutePath()).build();
        } else {
            return Response.noContent().build();
        }
    }


    /*
        I had written this before, when I originally planned to validate POST requests on whether or not the time was
        in the future, but decided not to use it in favor of more closely following the spec. See readme documentation
        on github for more.

        Waiting on clarification via email before making a decision on this. 
    */
    private boolean validateTransaction(Instant time, Instant now) {
        //This might be a little confusing, so just for clarity:
        //returns true if the time is both in the past and within 60 seconds of now
        return !(TimeUtil.isTimeInTheFuture(time, now) || TimeUtil.isTimeOlderThan60Seconds(time, now));
    }
}
