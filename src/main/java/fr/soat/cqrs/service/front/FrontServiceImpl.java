package fr.soat.cqrs.service.front;

import fr.soat.cqrs.dao.OrderDAO;
import fr.soat.cqrs.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FrontServiceImpl implements FrontService {

    private final OrderDAO orderDAO;

    @Autowired
    public FrontServiceImpl(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    @Transactional
    public Long order(Order order) {
        // save order in product_order
        return orderDAO.insert(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // delete order from product_order
        Order order = orderDAO.getById(orderId);
        orderDAO.delete(orderId);
    }


}
