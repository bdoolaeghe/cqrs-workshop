package fr.soat.cqrs.dao;

import fr.soat.cqrs.event.OrderEvent;

public interface OrderEventDAO {

    void pushOrderEvent(OrderEvent event);

    OrderEvent popOrderEvent();

}
