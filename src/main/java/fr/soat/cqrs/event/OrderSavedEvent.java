package fr.soat.cqrs.event;

import fr.soat.cqrs.model.Order;
import lombok.ToString;

@ToString(callSuper = true)
public class OrderSavedEvent extends OrderEvent {
    public OrderSavedEvent(Order order) {
        super(null, order);
    }
    public OrderSavedEvent(Long eventId, Order order) {
        super(eventId, order);
    }
}
