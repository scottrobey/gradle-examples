package org.sample.swaggen;

import java.net.URI;
import java.util.UUID;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;


class SwagGenerator {

    public static void main(String[] args) {

        try {
            final Server server = new Server();

            final ServerConnector connector = new ServerConnector(server);
            connector.setPort(0);

            server.addConnector(connector);

            final ServletContextHandler root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

            final ResourceConfig config = new ResourceConfig();
            config.packages("org.sample.swaggen");

            root.addServlet(new ServletHolder(new ServletContainer(config)), "/*");

            String uuid = UUID.randomUUID().toString();
            ShutdownHandler shutdown = new ShutdownHandler(uuid, false, true);

            server.setHandler(new HandlerList(shutdown, root));

            sysout("Starting Jetty Server..." );
            server.start();

            final URI serverURI = server.getURI();

            sysout("Server started at: " + serverURI);
            sysout("To Shutdown: curl -X POST http://localhost:" + serverURI.getPort() + "/shutdown?token=" + uuid);

        } catch (Throwable e) {
            syserr("Exception occurred", e);
        }
    }

    static void sysout(String msg) {
        System.out.println(msg);
    }
    static void syserr(String msg) {
        System.err.println(msg);
    }
    static void syserr(String msg, Throwable t) {
        syserr(msg);
        t.printStackTrace();
    }
}