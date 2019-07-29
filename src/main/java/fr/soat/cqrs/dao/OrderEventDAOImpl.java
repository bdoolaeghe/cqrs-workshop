package fr.soat.cqrs.dao;

import fr.soat.cqrs.event.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class OrderEventDAOImpl implements OrderEventDAO {

    public static final OrderEventMapper ORDER_EVENT_MAPPER = new OrderEventMapper();
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OrderEventDAOImpl(DataSource dataSource, ApplicationEventPublisher eventPublisher) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void pushOrderEvent(OrderEvent event) {
        String sql = "INSERT INTO order_event (event_type, product_order) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sql, event.getClass().getSimpleName(), OrderJsonMapper.toJason(event.getOrder()));
    }

    @Override
    public OrderEvent popOrderEvent() {
        String sql = "DELETE FROM order_event " +
                "WHERE event_id = (SELECT MIN(event_id) FROM order_event) " +
                "RETURNING event_id, event_type, product_order";
        return jdbcTemplate.queryForObject(sql, ORDER_EVENT_MAPPER);
    }
}
