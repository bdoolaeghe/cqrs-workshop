package fr.soat.cqrs.event;

import fr.soat.cqrs.model.Order;

public class OrderSavedEvent extends OrderEvent {
    public OrderSavedEvent(Order order) {
        super(order);
    }
}
