package org.sample.swaggen;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.ApplicationPath;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.json.JSONObject;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

import static java.lang.System.out;


/**
 * Sets up an embedded Jetty server with Jersey 2 and Swagger using an JAX-RS Application configuration
 * https://github.com/swagger-api/swagger-core/wiki/Swagger-Core-Jersey-2.X-Project-Setup-1.5#configure-and-initialize-swagger
 */
class SwagGenerator {

    /**
     * Usage:
     * --location=[file path]
     * --readonly-correction
     * --pretty
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            final String outputFileLocation = getArg(args, "--location");
            if(outputFileLocation.isEmpty()) {
                throw new RuntimeException("--location required argument not found");
            }

            final boolean readOnlyCorrection = getSwitchArg(args, "--readonly-correction");
            final boolean prettyPrint = getSwitchArg(args, "--pretty");

            final Server server = new Server();

            final ServerConnector connector = new ServerConnector(server);
            // pick a dynamic port
            connector.setPort(0);
            server.addConnector(connector);

            final ServletContextHandler root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

            ServletHolder restServlet =
                    new ServletHolder(new ServletContainer(new ApplicationConfig(SwagGenerator.class.getPackageName())));
            restServlet.setInitOrder(1);
            root.addServlet(restServlet, "/*");

            out.println("Starting Jetty Server...");
            server.start();

            int dynamicPort = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
            String swaggerUrl = "http://localhost:" + dynamicPort + "/swagger.json";
            // getURI() was taking several seconds on my mac, find the dynamic port instead
            //final URI serverURI = server.getURI();
            //final URI swaggerURI = serverURI.resolve("/swagger.json");

            out.println("Swagger URL: " + swaggerUrl);

            String swaggerJson = IOUtils.toString(new URL(swaggerUrl), Charset.defaultCharset());
            if (swaggerJson.isEmpty()) throw new Exception("Swagger JSON is empty!");

            if(readOnlyCorrection) {
                // change readOnly:true -> false, because: https://github.com/swagger-api/swagger-core/issues/2169
                swaggerJson = swaggerJson.replaceAll("\"readOnly\":true", "\"readOnly\":false");
            }

            if(prettyPrint) {
                swaggerJson = new JSONObject(swaggerJson).toString(4);
            }

            Path swaggerFilePath = Paths.get(outputFileLocation, "swagger.json");

            Files.write(swaggerFilePath, Arrays.asList(swaggerJson));
            out.println("Swagger JSON written to: " + swaggerFilePath.toFile().getAbsolutePath());

            server.stop();
            server.destroy();
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            t.printStackTrace();
            System.exit(-1);
        }
    }

    private static boolean getSwitchArg(final String[] args, final String switchArg) {
        final Optional<String> found = Arrays.stream(args).filter(arg -> arg.equals(switchArg)).findFirst();
        return found.isPresent();
    }

    private static String getArg(final String[] args, final String theArg) {
        final Optional<String> found = Arrays.stream(args).filter(arg -> arg.startsWith(theArg + "=")).findFirst();
        if(found.isPresent()) {
            final String foundArgument = found.get();
            return foundArgument.substring(foundArgument.indexOf("=")+1);
        }
        return "";
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