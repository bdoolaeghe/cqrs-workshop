package fr.soat.cqrs.service.front;

import fr.soat.cqrs.dao.OrderDAO;
import fr.soat.cqrs.dao.ProductInventoryDAO;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.OrderLine;
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
        Long orderId = orderDAO.insert(order);

        // update inventory
        //FIXME: for each order line, decrease the stock in product_inventory of the ordered quantity (ProductInventoryDAO.decreaseProductInventory())

        return orderId;
    }

}
