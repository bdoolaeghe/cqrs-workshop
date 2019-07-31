package com.mirakl.poc;

import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


@Slf4j
public class DatabaseChangeEventListenerEngine {

    public static void main(String[] args) throws InterruptedException {
        log.info("starting ....");
        // clean for tests
        File offsetDir = new File("/tmp/debezium");
        if (!offsetDir.exists()) {
            offsetDir.mkdir();
        }

//        File offsetDataFile = new File("/tmp/debezium/offset.dat");
//        if (offsetDataFile.exists()) {
//            offsetDataFile.delete();
//            System.out.println(offsetDataFile + " deleted");
//        }


        // Run the engine asynchronously ...
        Executor executor = Executors.newFixedThreadPool(2, new BasicThreadFactory.Builder().namingPattern("mirakl-cdc-engine-thread-%d").build());
        log.info("starting engine...");
        EmbeddedEngine catalogueEngine = newEmbeddedEngine("catalogue", "public.product", DatabaseChangeEventListenerEngine::handleEventProduct, "high_loaded_slot");
        ((ExecutorService) executor).submit(catalogueEngine);
        EmbeddedEngine shopEngine = newEmbeddedEngine("shop", "public.shop", DatabaseChangeEventListenerEngine::handleEventSheop, "light_loaded_slot");
        ((ExecutorService) executor).submit(shopEngine);


        // At some later time ...
        log.info("... waiting");
//        while(new Scanner(System.in) == null) {
        int count = 0;
        while(count < 10) {
            log.info("running (" + count + "/10)" + "...");
            Thread.sleep(60000);
//            count++;
        }

        log.info("time to stop now...");
//        shopEngine.stop();
//        catalogueEngine.stop();
        ((ExecutorService) executor).shutdown();
        System.exit(0);

    }

    private static EmbeddedEngine newEmbeddedEngine(String domain, String tableName, Consumer<SourceRecord> eventConsumer, String pgReplicationSlotName) {
        String offsetDatFile = "offset_" + domain + ".dat";
        String dbhistoryDatFile = "dbhistory_" + domain + ".dat";
        Configuration config = Configuration.create()
                /* begin engine properties */
                .with("connector.class",
                        "io.debezium.connector.postgresql.PostgresConnector")
                .with("offset.storage",
                        "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename",
                        "/tmp/debezium/" + offsetDatFile)
                .with("offset.flush.interval.ms", 1000)
//                .with("offset.flush.interval.ms", 60000)
                /* begin connector properties */
                .with("name", domain + "-postgresql-connector")
                .with("database.hostname", "localhost")
                .with("database.port", 5432)
                .with("database.user", "postgres")
//                .with("database.password", "postgres")
//                .with("server.id", 85744)
//                .with("database.server.name", "mydomain-connector")
                .with("database.dbname", "mirakl_dev")
                .with("table.whitelist", tableName)
                .with("database.history",
                        "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename",
                        "/tmp/debezium/" + dbhistoryDatFile)
                .with("slot.name", pgReplicationSlotName)
                .build();

        // Create the engine with this configuration ...
        System.out.println("building engine for " + tableName + "...");
        return EmbeddedEngine.create()
                .using(config)
                .notifying(eventConsumer)
                .build();

    }

//    static int count = 0;


    private static void handleEventProduct(SourceRecord productSourceRecord) {
        try {
            String op = ((Struct) productSourceRecord.value()).getString("op");
            if ("r".equals(op)) {
                Object after = ((Struct) productSourceRecord.value()).get("after");
                String productAfter = (after == null) ? null :  ((Struct) after).getString("product_title");
                System.out.println("-> snapshot > product is: " + productAfter );
            } else if ("u".equals(op)) {
                Object before = ((Struct) productSourceRecord.value()).get("before");
                String productBefore = (before == null) ? null :  ((Struct) before).getString("product_title");
                Object after = ((Struct) productSourceRecord.value()).get("after");
                String productAfter = (after == null) ? null :  ((Struct) after).getString("product_title");
                System.out.println("product has changed: " + productBefore + " -> " + productAfter);
            } else {
                System.out.println(productSourceRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void handleEventSheop(SourceRecord shopSourceRecord) {
        try {
//            count++;
            String op = ((Struct) shopSourceRecord.value()).getString("op");
            if ("r".equals(op)) {
                Object after = ((Struct) shopSourceRecord.value()).get("after");
                String shopNameAfter = (after == null) ? null :  ((Struct) after).getString("shop_name");
                System.out.println("-> snapshot > shop is: " + shopNameAfter );
            } else if ("u".equals(op)) {
                Object before = ((Struct) shopSourceRecord.value()).get("before");
                String shopNameBefore = (before == null) ? null :  ((Struct) before).getString("shop_name");
                Object after = ((Struct) shopSourceRecord.value()).get("after");
                String shopNameAfter = (after == null) ? null :  ((Struct) after).getString("shop_name");
                System.out.println("shop has changed: " + shopNameBefore + " -> " + shopNameAfter);
//                if (count == 2)
//                    throw new RuntimeException("boum shop");
            } else {
//                Object before = ((Struct) shopSourceRecord.value()).get("before");
//                Object after = ((Struct) shopSourceRecord.value()).get("after");

                System.out.println(shopSourceRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            throw e;
        }
    }
}
