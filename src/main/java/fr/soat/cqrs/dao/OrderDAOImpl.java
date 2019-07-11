package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.OrderLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

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
        String sql = "select product_order.id as order_id, " +
                "order_line.id as line_id, " +
                "order_line.reference, " +
                "order_line.quantity " +
                "from product_order inner join order_line " +
                "on order_line.order_id = product_order.id " +
                "where product_order.id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                new Object[] { orderId },
                new OrderMapper());
    }

    @Override
    @Transactional
    public void insert(Order order) {
        insertOrder(order);
        insertLines(order.getLines());
    }

    private void insertOrder(Order order) {
        String sql = "INSERT INTO product_order(id) VALUES (NEXTVAL('order_seq'))";
        int update = jdbcTemplate.update(sql);
        System.out.println(update);
    }

    private void insertLines(List<OrderLine> lines) {
        String sql = "INSERT INTO order_line(id, order_id, reference, quantity) " +
                "VALUES (NEXTVAL('line_seq'), CURRVAL('order_seq'), ?, ?)";
        for (OrderLine line : lines) {
            jdbcTemplate.update(sql, line.getProductReference(), line.getQuantity());
        }
    }
}
