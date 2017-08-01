package com.rrt.n26.api;

import com.rrt.n26.objects.Transaction;
import com.rrt.n26.stats.StatisticsCache;
import com.rrt.n26.stats.StatisticsCacheFactory;
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

    private final StatisticsCache stats = StatisticsCacheFactory.getInstance();

    /**
     * Handle HTTP POST requests.
     *
     * @return Empty HTTP Response 201 if the transaction is validated, or 204 otherwise
     */
    @POST
    public Response handlePost(@Context UriInfo ui, Transaction t) {
        if (transactionValidForPost(t, Instant.now())) {
            stats.addTransaction(t);
            return Response.created(ui.getAbsolutePath()).build();
        } else {
            return Response.noContent().build();
        }
    }

    private boolean transactionValidForPost(Transaction t, Instant n) {
        return t.getAmount() != null && t.getTimestamp() != null && !TimeUtil.isTimeOlderThan60Seconds(t.toInstant(), n);
    }
}
