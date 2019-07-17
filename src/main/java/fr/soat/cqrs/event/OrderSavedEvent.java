package fr.soat.cqrs.event;

import fr.soat.cqrs.model.Order;
import lombok.Getter;
import lombok.ToString;

@ToString
public class OrderSavedEvent {

    @Getter
    private final Order order;

    public OrderSavedEvent(Order order) {
        this.order = order;
    }
}
