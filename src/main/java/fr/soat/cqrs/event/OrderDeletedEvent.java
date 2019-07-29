package fr.soat.cqrs.event;

import fr.soat.cqrs.model.Order;

public class OrderDeletedEvent extends OrderEvent {
    public OrderDeletedEvent(Order order) {
        super(order);
    }
}
