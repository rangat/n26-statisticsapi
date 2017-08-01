package com.rrt.n26;

import com.rrt.n26.stats.StatisticsCache;
import com.rrt.n26.stats.StatisticsCacheFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatisticsServer {
    // Base URI the Grizzly HTTP server will listen on
    private static final URI BASE_URI = URI.create("http://localhost:8080/");

    //The following variables
    private static ResourceConfig config = null;
    private static ContextResolver<MoxyJsonConfig> moxyJsonResolver = null;

    /**
     * Main method. Creates the configurated server and logs errors.
     */
    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            final StatisticsCache stats = StatisticsCacheFactory.getInstance();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.shutdownNow();
                stats.shutdownScheduler();
            }));
            server.start();
            System.out.println(String.format("Application started.%nStop the application using CTRL+C"));

            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            Logger.getLogger(StatisticsServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /*
       Utility to create the HTTP Server
     */
    protected static HttpServer startServer() {
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, config(), false);
    }

    /*
        Utility to get resource configuration for Jersey and create a new one if it doesn't exist
     */
    private static ResourceConfig config() {
        if (config == null) {
            config = new ResourceConfig().packages("com.rrt.n26").register(moxyJsonResolver());
        }
        return config;
    }

    /*
        Utility to create ContextResolver for Moxy, which enables JSON support in the API
        Gets the resolver, or creates a new one if it doesn't exist
     */
    protected static ContextResolver<MoxyJsonConfig> moxyJsonResolver() {
        if (moxyJsonResolver == null) {
            final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
            Map<String, String> namespacePrefixMapper = new HashMap<>(1);
            namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
            moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
            moxyJsonResolver = moxyJsonConfig.resolver();
        }
        return moxyJsonResolver;
    }
}

