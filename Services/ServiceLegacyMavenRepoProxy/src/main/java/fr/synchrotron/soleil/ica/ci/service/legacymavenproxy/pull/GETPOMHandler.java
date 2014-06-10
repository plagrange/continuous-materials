package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pull;

import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.*;

/**
 * @author Gregory Boissinot
 */
public class GETPOMHandler extends GETHandler {

    public GETPOMHandler(Vertx vertx, HttpClient vertxHttpClient, String proxyPath, String repoHost, int repoPort, String repoUri) {
        super(vertx, vertxHttpClient, proxyPath, repoHost, repoPort, repoUri);
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final String path = repositoryRequestBuilder.buildRequestPath(request);
        System.out.println("Download " + path);

        HttpClientRequest vertxHttpClientRequest = vertxHttpClient.get(path, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse clientResponse) {

                int statusCode = clientResponse.statusCode();

                if (statusCode == HttpResponseStatus.NOT_FOUND.code()) {
                    request.response().setStatusCode(statusCode);
                    request.response().end();
                    return;
                }

                clientResponse.bodyHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer data) {
                        vertx.eventBus().sendWithTimeout(ServiceAddressRegistry.EB_ADDRESS_FIXLEGACYPOM_SERVICE, data.toString(), Integer.MAX_VALUE, new AsyncResultHandler<Message<String>>() {
                            @Override
                            public void handle(AsyncResult<Message<String>> asyncResult) {
                                if (asyncResult.succeeded()) {
                                    final Message<String> pomResultMessage = asyncResult.result();
                                    final String pomResultContent = pomResultMessage.body();
                                    if (pomResultContent == null) {
                                        request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                                        request.response().end();
                                    } else {
                                        request.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(pomResultContent.getBytes().length));
                                        request.response().end(pomResultContent);
                                    }
                                } else {
                                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                                    request.response().end(asyncResult.cause().toString());
                                }

                            }
                        });
                    }
                });

            }
        });

        //vertxHttpClientRequest.headers().set(request.headers());
        vertxHttpClientRequest.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {
                request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Exception from ").append(repositoryRequestBuilder.getRepoHost());
                errorMsg.append("-->").append(throwable.toString());
                errorMsg.append("\n");
                request.response().end(errorMsg.toString());
            }
        });
        vertxHttpClientRequest.end();

    }
}
