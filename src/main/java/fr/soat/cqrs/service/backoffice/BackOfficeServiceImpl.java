package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.dao.OrderDAO;
import fr.soat.cqrs.dao.ProductDAO;
import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackOfficeServiceImpl implements BackOfficeService {

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    @Autowired
    public BackOfficeServiceImpl(OrderDAO orderDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderDAO.getById(orderId);
    }

    @Override
    public BestSales getBestSales() {
        return productDAO.getBestSales();
    }
}
