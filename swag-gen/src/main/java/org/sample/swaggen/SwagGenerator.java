package org.sample.swaggen;

import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.ws.rs.ApplicationPath;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

import static java.lang.System.out;


/**
 * Sets up an embedded Jetty server with Jersey 2 and Swagger using an JAX-RS Application configuration
 * https://github.com/swagger-api/swagger-core/wiki/Swagger-Core-Jersey-2.X-Project-Setup-1.5#configure-and-initialize-swagger
 */
class SwagGenerator {

    public static void main(String[] args){
        try {
            // TODO: configure
            final String outputFileLocation = "";

            final Server server = new Server();

            final ServerConnector connector = new ServerConnector(server);
            // pick a dynamic port
            connector.setPort(0);
            server.addConnector(connector);

            final ServletContextHandler root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

            ServletHolder restServlet =
                    new ServletHolder(new ServletContainer(new ApplicationConfig(SwagGenerator.class.getPackage().getName())));
            restServlet.setInitOrder(1);
            root.addServlet(restServlet, "/*");

            out.println("Starting Jetty Server...");
            server.start();

            int dynamicPort = ((ServerConnector)server.getConnectors()[0]).getLocalPort();
            String swaggerUrl = "http://localhost:" + dynamicPort + "/swagger.json";
            // getURI() was taking several seconds on my mac, find the dynamic port instead
            //final URI serverURI = server.getURI();
            //final URI swaggerURI = serverURI.resolve("/swagger.json");

            out.println("Swagger URL: " + swaggerUrl);

            final String swaggerJson = IOUtils.toString(new URL(swaggerUrl), Charset.defaultCharset());
            if (swaggerJson.isEmpty()) throw new Exception("Swagger JSON is empty!");

            Path swaggerFilePath = Paths.get(outputFileLocation, "swagger.json");

            Files.write(swaggerFilePath, Arrays.asList(swaggerJson));
            out.println("Swagger JSON written to: " + swaggerFilePath.toFile().getAbsolutePath());

            server.stop();
            server.destroy();
        }
        catch(Throwable t) {
            System.err.println(t.getMessage());
            t.printStackTrace();
            System.exit(-1);
        }
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