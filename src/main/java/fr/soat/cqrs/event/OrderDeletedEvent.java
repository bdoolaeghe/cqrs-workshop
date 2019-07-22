package fr.soat.cqrs.event;

import fr.soat.cqrs.model.Order;
import lombok.Getter;
import lombok.ToString;

@ToString
public class OrderDeletedEvent {

    @Getter
    private final Order order;

    public OrderDeletedEvent(Order order) {
        this.order = order;
    }
}
