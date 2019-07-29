package fr.soat.cqrs.dao;

import fr.soat.cqrs.event.OrderDeletedEvent;
import fr.soat.cqrs.event.OrderEvent;
import fr.soat.cqrs.event.OrderSavedEvent;
import fr.soat.cqrs.model.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static fr.soat.cqrs.dao.OrderJsonMapper.fromJson;

public class OrderEventMapper implements RowMapper<OrderEvent> {

    public OrderEvent mapRow(ResultSet resultSet, int i) throws SQLException {
        Order order = fromJson(resultSet.getString("product_order"));
        String eventType = resultSet.getString("event_type");
        if (OrderDeletedEvent.class.getSimpleName().equals(eventType)) {
            return new OrderDeletedEvent(order);
        } else if (OrderSavedEvent.class.getSimpleName().equals(eventType)) {
            return new OrderSavedEvent(order);
        }

        throw new IllegalArgumentException("Unknown event type: " + eventType);
    }
}
