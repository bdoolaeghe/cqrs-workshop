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
    public void push(OrderEvent event) {
        // FIXME save event in order_event table !
        throw new RuntimeException("implement me !");
    }

    @Override
    public Optional<OrderEvent> pop() {
        // FIXME retrieve event from order_event table !
        throw new RuntimeException("implement me !");
    }
}
