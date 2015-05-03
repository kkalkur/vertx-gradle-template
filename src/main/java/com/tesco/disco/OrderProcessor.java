package com.tesco.disco;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import java.lang.String;


/**
 * Created by kkalkur on 03/05/2015.
 */
public class OrderProcessor extends BusModBase implements Handler<Message<JsonObject>> {
    protected final Logger log = LoggerFactory.getLogger(getClass());


    public void start() {
        super.start();

        logger.info("OrderProcessor Verticle started");

       String  address = "test.orderQueue.register";
       String processor_address = "test.OrderProcessor";

       eb.registerHandler(processor_address, this, new AsyncResultHandler<Void>() {
                public void handle(AsyncResult<Void> asyncResult) {
                    if (asyncResult.succeeded()) {
                        JsonObject msg = new JsonObject().putString("processor", processor_address);
                     eb.send(address , msg, new Handler<Message<JsonObject>>() {
                            @Override
                            public void handle(Message<JsonObject> message) {
                                logger.info("Registraion is complete : " + message.body().toString());
                            }
                        });
                    } else {
                        log.error("Not able to register the Process");
                }
                }
            });

    }

    @Override
    public void handle(Message<JsonObject> event) {

        log.info("The incoming message is " + event.body().toString());
        JsonObject response = new JsonObject();
        response.putString("OrderAck" ,"34566");
        event.reply(response);

    }
    }


