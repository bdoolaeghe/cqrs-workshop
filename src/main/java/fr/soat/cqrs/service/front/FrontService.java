package fr.soat.cqrs.service.front;

import fr.soat.cqrs.model.Order;

public interface FrontService {

    Long order(Order order);

    void cancelOrder(Long orderId);

}
