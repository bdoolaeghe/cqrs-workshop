package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.configuration.AppConfig;
import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.codehaus.plexus.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


@Slf4j
@Service
public class DatabaseChangeEventListener {

    private final AppConfig appConfig;

    @Autowired
    public DatabaseChangeEventListener(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

//    public static void main(String[] args) throws InterruptedException {
//        log.info("starting ....");
//        resetDebeziumOffset();
//
//        // Run the engine asynchronously ...
//        Executor executor = start();
//
//
//        // At some later time ...
//        log.info("... waiting");
////        while(new Scanner(System.in) == null) {
//        int count = 0;
//        while (count < 10) {
//            log.info("running (" + count + "/10)" + "...");
//            Thread.sleep(60000);
////            count++;
//        }
//
//        stop((ExecutorService) executor);
//        System.exit(0);
//
//    }

    public static void stop(ExecutorService executor) {
        log.info("time to stop now...");
//        shopEngine.stop();
//        catalogueEngine.stop();
        executor.shutdown();
    }

    public Executor start() {
        Executor executor = Executors.newFixedThreadPool(2, new BasicThreadFactory.Builder().namingPattern("cdc-engine-thread-%d").build());
        log.info("starting engine...");
        EmbeddedEngine productMarginEngine = newEmbeddedEngine("product_margin", "high_loaded_slot", DatabaseChangeEventListener::handleEventProduct);
        ((ExecutorService) executor).submit(productMarginEngine);
        EmbeddedEngine shopEngine = newEmbeddedEngine("shop", "light_loaded_slot", DatabaseChangeEventListener::handleEventSheop);
        ((ExecutorService) executor).submit(shopEngine);
        return executor;
    }

    public static void resetDebeziumOffset() {
        // we use kafka connect offset files backing store in /tmp/debezium for tests
        // to let debezium store the current WAL offset of "captured" changes

        // delete and recreate offset files folder
        try {
            File offsetDir = new File("/tmp/debezium");
            FileUtils.deleteDirectory(offsetDir);
        } catch (IOException e) {
            log.warn("Failed to clean offset files", e);
            ;
        }
    }

    private EmbeddedEngine newEmbeddedEngine(String tableName, String pgReplicationSlotName, Consumer<SourceRecord> eventConsumer) {
        String offsetDatFile = "offset_" + tableName + ".dat";
        String dbHistoryDatFile = "dbhistory_" + tableName + ".dat";
        Configuration config = buildConfiguration(tableName, pgReplicationSlotName, offsetDatFile, dbHistoryDatFile);

        // Create the engine with this configuration ...
        System.out.println("building engine for " + tableName + "...");
        return EmbeddedEngine.create()
                .using(config)
                .notifying(eventConsumer)
                .build();

    }

    private Configuration buildConfiguration(String tableName, String pgReplicationSlotName, String offsetDatFile, String dbhistoryDatFile) {
        return Configuration.create()
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
                .with("name", tableName + "-postgresql-connector")
                .with("database.hostname", appConfig.getDbHost())
                .with("database.port", appConfig.getDbPort())
                .with("database.user", appConfig.getDbUser())
                //                .with("database.password", "postgres")
                //                .with("server.id", 85744)
                //                .with("database.server.name", "mydomain-connector")
//                    .with("database.dbname", "mirakl_dev")
                .with("table.whitelist", tableName)
                .with("database.history",
                        "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename",
                        "/tmp/debezium/" + dbhistoryDatFile)
                .with("slot.name", pgReplicationSlotName)
                .build();
    }

//    static int count = 0;


    private static void handleEventProduct(SourceRecord productSourceRecord) {
        try {
            String op = ((Struct) productSourceRecord.value()).getString("op");
            if ("r".equals(op)) {
                Object after = ((Struct) productSourceRecord.value()).get("after");
                String productAfter = (after == null) ? null : ((Struct) after).getString("product_title");
                System.out.println("-> snapshot > product is: " + productAfter);
            } else if ("u".equals(op)) {
                Object before = ((Struct) productSourceRecord.value()).get("before");
                String productBefore = (before == null) ? null : ((Struct) before).getString("product_title");
                Object after = ((Struct) productSourceRecord.value()).get("after");
                String productAfter = (after == null) ? null : ((Struct) after).getString("product_title");
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
                String shopNameAfter = (after == null) ? null : ((Struct) after).getString("shop_name");
                System.out.println("-> snapshot > shop is: " + shopNameAfter);
            } else if ("u".equals(op)) {
                Object before = ((Struct) shopSourceRecord.value()).get("before");
                String shopNameBefore = (before == null) ? null : ((Struct) before).getString("shop_name");
                Object after = ((Struct) shopSourceRecord.value()).get("after");
                String shopNameAfter = (after == null) ? null : ((Struct) after).getString("shop_name");
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
