package com.tesco.disco;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;


/**
 * Created by kkalkur on 02/05/2015.
 */
public class DeployVerticle extends Verticle {


    public void start() {

        final Logger logger = container.logger();

        logger.info("DeployVerticle started");
        try {
            JsonObject apconfig = container.config();
            JsonObject verticle1Config = apconfig.getObject("verticle_queue_conf");
            container.deployModule("io.vertx~mod-work-queue~2.0.0-final", verticle1Config, 10, new AsyncResultHandler<String>() {
                public void handle(AsyncResult<String> asyncResult) {
                    if (asyncResult.succeeded()) {
                        System.out.println("The Module has been deployed, deployment ID is " + asyncResult.result());
                    } else {
                        asyncResult.cause().printStackTrace();
                    }
                }
            }) ;
          /*  container.deployVerticle("com.tesco.disco.OrderProcessor", 4, new AsyncResultHandler<String>() {
                public void handle(AsyncResult<String> asyncResult) {
                    if (asyncResult.succeeded()) {
                        System.out.println("The OrderProcessor verticle has been deployed, deployment ID is " + asyncResult.result());
                    } else {
                        asyncResult.cause().printStackTrace();
                    }
                }
            });
            */

            container.deployWorkerVerticle("com.tesco.disco.OrderProcessor", null, 4, true, new AsyncResultHandler<String>() {
                public void handle(AsyncResult<String> asyncResult) {
                    if (asyncResult.succeeded()) {
                        System.out.println("The OrderProcessor verticle has been deployed, deployment ID is " + asyncResult.result());
                    } else {
                        asyncResult.cause().printStackTrace();
                    }
                }
            });

            container.deployVerticle("com.tesco.disco.SimpleHttpServer", new AsyncResultHandler<String>() {
                public void handle(AsyncResult<String> asyncResult) {
                    if (asyncResult.succeeded()) {
                        System.out.println("The SimpleHttpServer  verticle has been deployed, deployment ID is " + asyncResult.result());
                    } else {
                        asyncResult.cause().printStackTrace();
                    }
                }
            });
            JsonObject verticle2Config = apconfig.getObject("verticle_watcher");
            System.out.println(verticle2Config.toString());

            container.deployVerticle("com.tesco.disco.WatcherVerticle",verticle2Config,1, new AsyncResultHandler<String>() {
                public void handle(AsyncResult<String> asyncResult) {
                    if (asyncResult.succeeded()) {
                        System.out.println("The WatcherVerticle  verticle has been deployed, deployment ID is " + asyncResult.result());
                    } else {
                        asyncResult.cause().printStackTrace();
                    }
                }
            });

        } catch(Exception e){
            e.printStackTrace();
        }


    }

}
