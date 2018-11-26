package org.sample.swaggen;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.ApplicationPath;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;


/**
 * Sets up an embedded Jetty server with Jersey 2 and Swagger using an JAX-RS Application configuration
 * https://github.com/swagger-api/swagger-core/wiki/Swagger-Core-Jersey-2.X-Project-Setup-1.5#configure-and-initialize-swagger
 */
class SwagGenerator {

    public static void main(String[] args) {
        final String uuid = UUID.randomUUID().toString();

        try {
            final Server server = new Server();

            final ServerConnector connector = new ServerConnector(server);
            // pick a dynamic port
            connector.setPort(0);
            server.addConnector(connector);

            final ServletContextHandler root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

            ServletHolder restServlet = new ServletHolder(new ServletContainer(new ApplicationConfig(SwagGenerator.class.getPackage().getName())));
            restServlet.setInitOrder(1);
            root.addServlet(restServlet, "/*");

            // setup shutdown handler
            ShutdownHandler shutdown = new ShutdownHandler(uuid, false, true);
            server.setHandler(new HandlerList(shutdown, root));

            sysout("Starting Jetty Server..." );
            server.start();

            final URI serverURI = server.getURI();

            sysout("Server started at: " + serverURI);
            sysout("Swagger served at: " + serverURI + "swagger.json");
            sysout("To Shutdown: curl -X POST http://localhost:" + serverURI.getPort() + "/shutdown?token=" + uuid);

            server.join();

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

    @ApplicationPath("/swagger")
    static class ApplicationConfig extends ResourceConfig {
        public ApplicationConfig(String ... pkgs) {
            configureSwagger(pkgs);
        }
        public void configureSwagger(String ... pkgs) {
            register(ApiListingResource.class);
            register(SwaggerSerializers.class);

            final String packageStr = String.join(",", pkgs);

            final BeanConfig beanConfig = new BeanConfig();
            beanConfig.setTitle("Test API");
            beanConfig.setDescription("Test Rest API");
            beanConfig.setVersion("0.0.1");
            beanConfig.setBasePath("/*");
            beanConfig.setResourcePackage(packageStr);
            beanConfig.setScan(true);

            packages(packageStr);
        }
    }
}