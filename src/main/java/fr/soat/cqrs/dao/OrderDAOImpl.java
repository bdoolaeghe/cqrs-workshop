package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class OrderDAOImpl implements OrderDAO {

    JdbcTemplate jdbcTemplate;

    private final String SQL_FIND_BY_ID = "select product_order.id as order_id, " +
            "order_line.id as line_id, " +
            "order_line.reference, " +
            "order_line.quantity " +
            "from product_order inner join order_line " +
            "on order_line.order_id = product_order.id " +
            "where product_order.id = ?";

    @Autowired
    public OrderDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Order getById(Long orderId) {
        return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[] { orderId }, new OrderMapper());
    }
}
