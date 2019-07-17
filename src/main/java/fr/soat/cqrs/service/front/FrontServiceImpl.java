package fr.soat.cqrs.service.front;

import fr.soat.cqrs.dao.OrderDAO;
import fr.soat.cqrs.dao.ProductDAO;
import fr.soat.cqrs.dao.ProductMarginDAO;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.OrderLine;
import fr.soat.cqrs.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FrontServiceImpl implements FrontService {

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final ProductMarginDAO productMarginDAO;

    @Autowired
    public FrontServiceImpl(OrderDAO orderDAO, ProductDAO productDAO, ProductMarginDAO productMarginDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        this.productMarginDAO = productMarginDAO;
    }

    @Override
    @Transactional
    public Long order(Order order) {
        // 1. save order in product_order
        Long inserted = orderDAO.insert(order);

        for (OrderLine orderLine : order.getLines()) {
            // 2. for each product, compute the margin to add
            Long productReference = orderLine.getProductReference();
            int quantity = orderLine.getQuantity();
            Product product = productDAO.getByReference(productReference);
            float productMargin = Math.round((product.getPrice() - product.getSupplyPrice()) * quantity);

            // 3. update total_margin in product_margin table
            productMarginDAO.incrementProductMargin(productReference, product.getName(), productMargin);
        }
        return inserted;
    }

}
