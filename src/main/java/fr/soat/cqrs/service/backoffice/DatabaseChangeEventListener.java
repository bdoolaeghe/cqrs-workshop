package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.configuration.AppConfig;
import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


@Slf4j
@Service
public class DatabaseChangeEventListener {

    private final AppConfig appConfig;
    private ExecutorService executor = null;
    private final AtomicInteger slotCounter = new AtomicInteger(1);

    @Autowired
    public DatabaseChangeEventListener(AppConfig appConfig) {
        this.appConfig = appConfig;
        checkOffsetConnectorFolder();
    }

    private void checkOffsetConnectorFolder() {
        File offsetDir = new File("/tmp/debezium");
        if (!offsetDir.exists())
            log.warn("missing " + offsetDir +", creating...");
        offsetDir.mkdir();
    }

    public void startListener(String tableName, Consumer<SourceRecord> sourceRecordConsumer) {
        log.info("starting listener engine fro table {}...", tableName);
        String slotName = "my_slot_" + slotCounter.getAndIncrement();
        EmbeddedEngine engine = newEmbeddedEngine(tableName, slotName, sourceRecordConsumer);
        if (executor == null) {
            executor = Executors.newFixedThreadPool(2, new BasicThreadFactory.Builder().namingPattern("cdc-engine-thread-%d").build());
        }
        ((ExecutorService) executor).submit(engine);
    }

    public void stopListeners() {
        log.info("stopping engine(s)...");
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }

    private EmbeddedEngine newEmbeddedEngine(String tableName, String pgReplicationSlotName, Consumer<SourceRecord> eventConsumer) {
        String offsetDatFile = "offset_" + tableName + ".dat"; // persist current offset in WAL of already captured data changes
        String dbHistoryDatFile = "dbhistory_" + tableName + ".dat"; // persist current offset in WAL of already captured schema changes
        Configuration config = buildConfiguration(tableName, pgReplicationSlotName, offsetDatFile, dbHistoryDatFile);

        // Create the engine with this configuration ...
        log.info("building engine for " + tableName + "...");
        return EmbeddedEngine.create()
                .using(config)
                .notifying(eventConsumer)
                .build();

    }

    private Configuration buildConfiguration(String tableName, String pgReplicationSlotName, String offsetDatFile, String dbHistoryDatFile) {
        return Configuration.create()
                /* begin engine properties */
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "/tmp/debezium/" + offsetDatFile)
                .with("offset.flush.interval.ms", 100)
                /* begin connector properties */
                .with("name", tableName + "-postgresql-connector")
                .with("database.hostname", appConfig.getDbHost())
                .with("database.port", appConfig.getDbPort())
                .with("database.user", appConfig.getDbUser())
                .with("database.server.name", "my-cqrs-workshop-connector")
                .with("database.dbname", "postgres")
                .with("table.whitelist", tableName)
                .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename", "/tmp/debezium/" + dbHistoryDatFile)
                .with("slot.name", pgReplicationSlotName)
                .build();
    }

}
