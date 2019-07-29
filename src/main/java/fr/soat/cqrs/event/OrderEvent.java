package fr.soat.cqrs.event;

import fr.soat.cqrs.model.Order;
import lombok.Getter;
import lombok.ToString;

@ToString
public class OrderEvent {

        @Getter
        private final Order order;
        @Getter
        private final Long eventId;

        public OrderEvent(Long eventId, Order order) {
            this.eventId = eventId;
            this.order = order;
        }

}
