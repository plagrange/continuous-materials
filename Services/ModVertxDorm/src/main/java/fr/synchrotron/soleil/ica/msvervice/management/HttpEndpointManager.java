package fr.synchrotron.soleil.ica.msvervice.management;

import fr.synchrotron.soleil.ica.msvervice.management.handlers.POMExportHandler;
import fr.synchrotron.soleil.ica.msvervice.management.handlers.POMImportHandler;
import fr.synchrotron.soleil.ica.msvervice.vertx.lib.utilities.VertxConfigLoader;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author Gregory Boissinot
 */
public class HttpEndpointManager extends Verticle {


    @Override
    public void start() {

        try {
            final EventBus eventBus = vertx.eventBus();

            final AsyncResultHandler<String> asyncResultHandler = new AsyncResultHandler<String>() {
                @Override
                public void handle(AsyncResult<String> asyncResult) {
                    onLoaded(asyncResult);
                }
            };

            final VertxConfigLoader vertxConfigLoader = new VertxConfigLoader();
            final JsonObject config = vertxConfigLoader.createConfig(container.config());
            //Deploy mod-mavenmetadata (a runnable module and nested module)
            container.deployModule("fr.synchrotron.soleil~mod-mavenmetadata~1.0.0", config, 1, asyncResultHandler);

            RouteMatcher routeMatcher = new RouteMatcher();

            //-- POM IMPORTER
            final POMImportHandler pomImportHandler = new POMImportHandler(eventBus);
            routeMatcher.post("/pom/import", pomImportHandler);
            routeMatcher.put("/pom/import", pomImportHandler);

            //--POM EXPORTER
            final POMExportHandler pomExportHandler = new POMExportHandler(eventBus);
            routeMatcher.post("/pom/export", pomExportHandler);

            routeMatcher.allWithRegEx(".*", new Handler<HttpServerRequest>() {
                @Override
                public void handle(HttpServerRequest request) {
                    request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
                    request.response().end("Path or Http method not supported.\n");
                }
            });

            final int serverPort = getServerPort(config);
            vertx.createHttpServer().requestHandler(routeMatcher).listen(serverPort);
            container.logger().info("Webserver  started on " + serverPort);

        } catch (Throwable e) {
            container.logger().error("Can't start the main verticle: " + e.getMessage());
        }
    }

    private int getServerPort(JsonObject config) {
        final Integer port = config.getInteger("port");
        if (port == null) {
            throw new ConfigurationException("A port number is required");
        }
        return port;
    }

    private void onLoaded(AsyncResult<String> asyncResult) {
        if (!asyncResult.succeeded()) {
            container.logger().info(asyncResult.cause());
        }
    }

}