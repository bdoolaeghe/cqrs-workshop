package fr.soat.cqrs.event;

import fr.soat.cqrs.model.Order;
import lombok.ToString;

@ToString(callSuper = true)
public class OrderDeletedEvent extends OrderEvent {
    public OrderDeletedEvent(Order order) {
        super(null, order);
    }
    public OrderDeletedEvent(Long eventId, Order order) {
        super(eventId, order);
    }
}
