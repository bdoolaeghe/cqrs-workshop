package fr.soat.cqrs.service.backoffice;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductListener {

    private final DatabaseChangeEventListener databaseChangeEventListener;

    public ProductListener(DatabaseChangeEventListener databaseChangeEventListener) {
        this.databaseChangeEventListener = databaseChangeEventListener;
    }

    public void start() {
        databaseChangeEventListener.startListener("public.product", this::onProductRecord);
        log.info(this.getClass().getSimpleName() + " is started (start consuming events)");
    }

    public void stop() {
        databaseChangeEventListener.stopListeners();
        log.info(this.getClass().getSimpleName() + " is stopped (stop consuming events)");
    }

    private void onProductRecord(SourceRecord productOrderSourceRecord) {
        log.info("SourceRecord received: {}", productOrderSourceRecord);
        try {
            Struct recordValue = (Struct) productOrderSourceRecord.value();
            if (recordValue != null) {
                String op = recordValue.getString("op");
                if ("r".equals(op)) {
                    // snapshot
                    Object rowAfter = ((Struct) productOrderSourceRecord.value()).get("after");
                    System.out.println("-> snapshot > row is: " + rowAfter);
                } else if ("c".equals(op)) {
                    //insert
                    Object rowAfter = ((Struct) productOrderSourceRecord.value()).get("after");
                    System.out.println("row inserted: " + rowAfter);
                } else if ("u".equals(op)) {
                    // update
                    Object rowBefore = ((Struct) productOrderSourceRecord.value()).get("before");
                    Object rowAfter = ((Struct) productOrderSourceRecord.value()).get("after");
                    System.out.println("row has changed: " + rowBefore + " -> " + rowAfter);
                } else if ("d".equals(op)) {
                    // delete
                    Object rowBefore = ((Struct) productOrderSourceRecord.value()).get("before");
                    System.out.println("row deleted: " + rowBefore);
                } else {
                    log.warn("Received unsupported record: {}", productOrderSourceRecord);
                }
            }
        } catch (Exception e) {
            log.error("Failed to consume SourceRecord", e);
        }
    }

}
