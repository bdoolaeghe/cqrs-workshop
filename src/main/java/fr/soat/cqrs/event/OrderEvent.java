package fr.soat.cqrs.event;

import fr.soat.cqrs.model.Order;
import lombok.Getter;
import lombok.ToString;

@ToString
public class OrderEvent {

        @Getter
        private final Order order;

        public OrderEvent(Order order) {
            this.order = order;
        }

}
