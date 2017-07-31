package com.rrt.n26.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("statistics")
public class StatisticsAPI {

    /**
     *
     * Handles HTTP GET Requests.
     *
     * @return for now, a basic stub
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String handleGet() {
        return "Got\n";
    }
}
