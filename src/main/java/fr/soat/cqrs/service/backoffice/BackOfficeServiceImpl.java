package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.dao.OrderDAO;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackOfficeServiceImpl implements BackOfficeService {

    private final OrderDAO orderDAO;

    @Autowired
    public BackOfficeServiceImpl(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderDAO.getById(orderId);
    }

    @Override
    public List<Product> getBestSales() {
        throw new RuntimeException("implement me !");
    }
}
