package com.rrt.n26;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("transactions")
public class TransactionsAPI {

    /**
     * Handle HTTP POST requests.
     *
     * @return for now, a basic stub
     */
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String handlePost() {
        return "Posted";
    }
}
