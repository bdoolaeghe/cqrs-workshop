package fr.soat.cqrs.dao;

import fr.soat.cqrs.event.OrderEvent;

import java.util.Optional;

public interface OrderEventDAO {

    void pushOrderEvent(OrderEvent event);

    Optional<OrderEvent> popOrderEvent();

}
