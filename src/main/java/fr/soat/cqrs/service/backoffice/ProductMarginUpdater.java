package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.dao.ProductDAO;
import fr.soat.cqrs.dao.ProductMarginDAO;
import fr.soat.cqrs.event.OrderDeletedEvent;
import fr.soat.cqrs.event.OrderSavedEvent;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.OrderLine;
import fr.soat.cqrs.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductMarginUpdater {

    private final ProductDAO productDAO;
    private final ProductMarginDAO productMarginDAO;
    private final DatabaseChangeEventListener databaseChangeEventListener;

    public ProductMarginUpdater(ProductDAO productDAO, ProductMarginDAO productMarginDAO, DatabaseChangeEventListener databaseChangeEventListener) {
        this.productDAO = productDAO;
        this.productMarginDAO = productMarginDAO;
        this.databaseChangeEventListener = databaseChangeEventListener;
    }

    public void start() {
        databaseChangeEventListener.startListener("public.product", this::onProductOrderRecord);
        log.info(this.getClass().getSimpleName() + " is started (start consuming events)");
    }

    public void stop() {
        databaseChangeEventListener.stopListeners();
        log.info(this.getClass().getSimpleName() + " is stopped (stop consuming events)");
    }

    private void onProductOrderRecord(SourceRecord productOrderSourceRecord) {
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

    private void onOrderSavedEvent(OrderSavedEvent orderSavedEvent) {
        log.info("Consuming " + orderSavedEvent);
        Order order  = orderSavedEvent.getOrder();

        for (OrderLine orderLine : order.getLines()) {
            // 2. for each product, compute the margin to add
            Long productReference = orderLine.getProductReference();
            int quantity = orderLine.getQuantity();
            Product product = productDAO.getByReference(productReference);
            float productMargin = Math.round((product.getPrice() - product.getSupplyPrice()) * quantity);

            // 3. update total_margin in product_margin table
            productMarginDAO.incrementProductMargin(productReference, product.getName(), productMargin);
        }
    }

    private void onOrderDeletedEvent(OrderDeletedEvent orderDeletedEvent) {
        log.info("Consuming " + orderDeletedEvent);
        Order order  = orderDeletedEvent.getOrder();

        for (OrderLine orderLine : order.getLines()) {
            // 2. for each product, compute the margin to remove
            Long productReference = orderLine.getProductReference();
            int quantity = orderLine.getQuantity();
            Product product = productDAO.getByReference(productReference);
            float productMargin = Math.round((product.getPrice() - product.getSupplyPrice()) * quantity);

            // 3. update total_margin in product_margin table
            productMarginDAO.decrementProductMargin(productReference, product.getName(), productMargin);
        }
    }

}
