package com.tesco.disco;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Created by kkalkur on 03/05/2015.
 */
public class WatcherVerticle extends BusModBase implements Handler<Message<JsonObject>> {

    JsonObject config =  null;
    private WatchService watcher;
    private final WatchKey watchKey=null ;

    public void start() {
        final Logger logger = container.logger();
        super.start();
        String  address = "test.wathcer";
        config = container.config();

        eb.registerHandler(address, this, new AsyncResultHandler<Void>() {
            public void handle(AsyncResult<Void> asyncResult) {
                if (asyncResult.succeeded()) {
                    logger.info("Registration succeded");
                } else {
                    logger.error("Not able to register the Process");
                }
            }
        });
        logger.info("WatcherVerticle started");
        checkdirectories(config);

        long timerID = vertx.setPeriodic(7000, new Handler<Long>() {
            public void handle(Long timerID) {
                logger.info("And every second this is printed");
                WatchKey key;
                try {
                    key = watcher.poll();

                    if (key == null) {
                        return;
                    }

                  for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        // an OVERFLOW event can occur regardless if events are lost or discarded.
                        if (kind == OVERFLOW) {
                            logger.warn("OVERFLOW");
                        }

                        // The filename is the context of the event.
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;

                        Path filename = ev.context();
                      logger.info("File is name succeded");


                    }
                } catch (ClosedWatchServiceException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        logger.info("First this is printed");
    }

    private void checkdirectories(JsonObject verticle1Config){
        logger.info("The Directory Name is "+verticle1Config);
        Path dirPath = Paths.get(verticle1Config.getString("directory"));
        try {
            // Check if path is a folder
            Boolean isFolder = (Boolean) Files.getAttribute(dirPath, "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                dirPath = Paths.get(dirPath.toString().replace(dirPath.getFileName().toString(), ""));
            }
            watcher = FileSystems.getDefault().newWatchService();

                WatchKey dirKey = dirPath.register(watcher,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY);


            logger.info("Watcher registered for: " +dirPath.getFileName().toString());

        } catch (NoSuchFileException e) {
            logger.warn("No file: ");

        } catch (IllegalArgumentException e) {
            logger.warn("Couldn't resolve provided path: ") ;
        } catch (ClosedWatchServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handle(Message<JsonObject> event) {

        logger.info("The incoming message is " + event.body().toString());


    }


}
