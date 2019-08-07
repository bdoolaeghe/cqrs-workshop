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
        //FIXME start a listener for table order_line, using callback onOrderLineRecord()
        throw new RuntimeException("implement me !");
    }

    public void stop() {
        databaseChangeEventListener.stopListeners();
        log.info(this.getClass().getSimpleName() + " is stopped (stop consuming events)");
    }

    private void onOrderLineRecord(SourceRecord orderLineRecord) {
        log.info("SourceRecord received: {}", orderLineRecord);
        try {
            //FIXME
            // 1. parse the record to react on snapshot/insert operations (check ProductListener#onProductRecord for an example)
            // 2. get the product *reference* from the record
            // 3. load the product by *reference* using `ProductDAO`
            // 4. upsert a row in `order_report` with product reference, name, price, and current Date (Use [OrderReportDAO#upsert()](src/main/java/fr/soat/cqrs/dao/OrderReportDAO.java#L7))
            throw new RuntimeException("implement me !");
        } catch (Exception e) {
            log.error("Failed to consume SourceRecord", e);
        }
    }
}
