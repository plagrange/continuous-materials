package fr.synchrotron.soleil.ica.proxy.utilities;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpHeaders;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class RequestHandlerWrapper implements Handler<HttpServerRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandlerWrapper.class);
    private final RouteMatcher routeMatcher;

    public RequestHandlerWrapper(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }

    @Override
    public void handle(final HttpServerRequest request) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Incoming request : " + request.method() + " " + request.uri());
        }

        request.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable t) {
                LOG.error("Severe error during request processing :", t);
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                request.response().end();
            }
        });

        try {
            cleanRequestHttpHeaders(request);
            routeMatcher.handle(request);
        } catch (Throwable t) {
            LOG.error("The routeMatcher throw an error", t);
            LOG.error("Severe error during request processing :", t);
            request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            request.response().end();
        }
    }

    private void cleanRequestHttpHeaders(HttpServerRequest request) {
        final MultiMap headers = request.headers();
        for (Map.Entry<String, String> header : headers) {
            String headerValue = header.getValue();
            if (headerValue == null) {
                headers.remove(header.getKey());
            }
        }
        headers.remove(HttpHeaders.KEEP_ALIVE);
        headers.remove(HttpHeaders.CONNECTION);  //not necessary with keepAlive to false from clients

    }

}
