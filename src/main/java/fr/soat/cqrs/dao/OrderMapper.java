package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.OrderLine;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {

    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        Order order = new Order();
        while (resultSet.next()) {
            OrderLine line = new OrderLine();
            line.setId(resultSet.getLong("line_id"));
            line.setProductReference(resultSet.getLong("reference"));
            line.setQuantity(resultSet.getInt("quantity"));
            order.setId(resultSet.getLong("order_id"));
            order.getLines().add(line);
        }
        return order;
    }
}