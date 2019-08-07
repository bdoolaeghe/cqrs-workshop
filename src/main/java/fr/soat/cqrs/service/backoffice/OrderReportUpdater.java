package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.dao.OrderReportDAO;
import fr.soat.cqrs.dao.ProductDAO;
import fr.soat.cqrs.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class OrderReportUpdater {

    private final DatabaseChangeEventListener databaseChangeEventListener;
    private final ProductDAO productDAO;
    private final OrderReportDAO orderReportDAO;

    public OrderReportUpdater(DatabaseChangeEventListener databaseChangeEventListener, ProductDAO productDAO, OrderReportDAO orderReportDAO) {
        this.databaseChangeEventListener = databaseChangeEventListener;
        this.productDAO = productDAO;
        this.orderReportDAO = orderReportDAO;
    }

    public void start() {
        databaseChangeEventListener.startListener("public.order_line", this::onOrderLineRecord);
        log.info(this.getClass().getSimpleName() + " is started (start consuming events)");
    }

    public void stop() {
        databaseChangeEventListener.stopListeners();
        log.info(this.getClass().getSimpleName() + " is stopped (stop consuming events)");
    }

    private void onOrderLineRecord(SourceRecord orderLineRecord) {
        log.info("SourceRecord received: {}", orderLineRecord);
        try {
            Struct recordValue = (Struct) orderLineRecord.value();
            if (recordValue != null) {
                String op = recordValue.getString("op");
                if ("r".equals(op) || "c".equals(op)) {
                    //insert / snapshot
                    Struct row = (Struct) ((Struct) orderLineRecord.value()).get("after");
                    long reference = Long.valueOf((int)row.get("reference"));
                    Product product = productDAO.getByReference(reference);

                    // 2. update order_report
                    orderReportDAO.upsert(reference, product.getName(), product.getPrice(), LocalDate.now());
                    log.info("adding in report {}", product.getName());
                } else {
                    log.warn("Received unsupported record: {}", orderLineRecord);
                }
            }
        } catch (Exception e) {
            log.error("Failed to consume SourceRecord", e);
        }
    }
}
