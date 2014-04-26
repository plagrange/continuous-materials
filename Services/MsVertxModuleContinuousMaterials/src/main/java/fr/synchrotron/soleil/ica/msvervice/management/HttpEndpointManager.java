package fr.synchrotron.soleil.ica.msvervice.management;

import fr.synchrotron.soleil.ica.msvervice.management.handlers.POMExportHandler;
import fr.synchrotron.soleil.ica.msvervice.management.handlers.POMImportHandler;
import fr.synchrotron.soleil.ica.msvervice.vertx.verticle.pomexporter.POMExporterWorkerVerticle;
import fr.synchrotron.soleil.ica.msvervice.vertx.verticle.pomimport.POMImporterWorkerVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Gregory Boissinot
 */
public class HttpEndpointManager extends Verticle {

    public static final long SEND_MS_TIMEOUT = 10 * 1000l; // in ms

    private static final String MONGODB_PROPERTIES_FILEPATH = "/infra.properties";

    @Override
    public void start() {

        try {
            final EventBus eventBus = vertx.eventBus();

            //-- Deploy Required Verticle
            final AsyncResultHandler<String> asyncResultHandler = new AsyncResultHandler<String>() {
                @Override
                public void handle(AsyncResult<String> asyncResult) {
                    onVerticleLoaded(asyncResult);
                }
            };

            final JsonObject jsonObject = createConfig();
            container.deployWorkerVerticle(
                    POMImporterWorkerVerticle.class.getCanonicalName(),
                    jsonObject, 1, true, asyncResultHandler);
            container.deployWorkerVerticle(
                    POMExporterWorkerVerticle.class.getCanonicalName(),
                    jsonObject, 1, true, asyncResultHandler);

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

            final int serverPort = getServerPort(container.config());
            vertx.createHttpServer().requestHandler(routeMatcher).listen(serverPort);
            container.logger().info("Webserver  started on " + serverPort);

        } catch (Throwable e) {
            container.logger().error(e.getMessage());
        }
    }

    private int getServerPort(JsonObject config) {
        final Integer port = config.getInteger("port");
        if (port == null) {
            throw new ConfigurationException("A port number is required");
        }
        return port;
    }

    private void onVerticleLoaded(AsyncResult<String> asyncResult) {
        if (!asyncResult.succeeded()) {
            container.logger().info(asyncResult.cause());
        }
    }

    private JsonObject createConfig() {
        final JsonObject config = container.config();
        Properties properties = loadInfraFile(MONGODB_PROPERTIES_FILEPATH);
        for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
            String propKey = (String) objectObjectEntry.getKey();
            if (!config.containsField(propKey)) {
                String propValue = (String) objectObjectEntry.getValue();
                config.putString(propKey, propValue);
            }
        }
        return config;
    }

    private Properties loadInfraFile(String propertiedFilePath) {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream(propertiedFilePath));
            return properties;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
