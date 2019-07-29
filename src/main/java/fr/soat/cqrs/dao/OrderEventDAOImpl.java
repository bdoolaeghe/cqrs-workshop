package fr.soat.cqrs.dao;

import fr.soat.cqrs.event.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class OrderEventDAOImpl implements OrderEventDAO {

    public static final OrderEventMapper ORDER_EVENT_MAPPER = new OrderEventMapper();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderEventDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void pushOrderEvent(OrderEvent event) {
        String sql = "INSERT INTO order_event (event_type, product_order) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sql, event.getClass().getSimpleName(), OrderJsonMapper.toJason(event.getOrder()));
    }

    @Override
    public Optional<OrderEvent> popOrderEvent() {
        String sql = "DELETE FROM order_event " +
                "WHERE event_id = (SELECT MIN(event_id) FROM order_event) " +
                "RETURNING event_id, event_type, product_order";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, ORDER_EVENT_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
