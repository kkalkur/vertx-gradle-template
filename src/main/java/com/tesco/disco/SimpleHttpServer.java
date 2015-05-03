package com.tesco.disco;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import java.util.Map;

/**
 * Created by kkalkur on 03/05/2015.
 */
public class SimpleHttpServer extends Verticle {

    public void start() {

        final Logger logger = container.logger();

        logger.info("HttpServer Verticle started ");

        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest request) {
                logger.info("A request has arrived on the server!");
                EventBus eb = vertx.eventBus();
                JsonObject msg = new JsonObject().putString("OrderNumber", "200");
                eb.send("test.orderQueue", msg, new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> message) {
                        logger.info("Response is complete : " + message.body().toString());
                    }
                });

                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> header : request.headers().entries()) {
                    sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
                }
                request.response().putHeader("content-type", "text/plain");
                request.response().end(sb.toString());

            }
        }).listen(8083, "localhost");


    }
}
