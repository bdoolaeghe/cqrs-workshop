package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.dao.OrderDAO;
import fr.soat.cqrs.dao.ProductMarginDAO;
import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackOfficeServiceImpl implements BackOfficeService {

    private final OrderDAO orderDAO;
    private final ProductMarginDAO productMarginDAO;

    @Autowired
    public BackOfficeServiceImpl(OrderDAO orderDAO, ProductMarginDAO productMarginDAO) {
        this.orderDAO = orderDAO;
        this.productMarginDAO = productMarginDAO;
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderDAO.getById(orderId);
    }

    @Override
    public BestSales getBestSales() {
        return productMarginDAO.getBestSales();
    }
}
