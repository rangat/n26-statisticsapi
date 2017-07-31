package com.rrt.n26.api;

import com.rrt.n26.jsonobjects.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionsAPI {

    /**
     * Handle HTTP POST requests.
     *
     * @return for now, a basic stub
     */
    @POST
    public Transaction handlePost(Transaction t) {
        System.out.println(t);
        return t;
    }
}
