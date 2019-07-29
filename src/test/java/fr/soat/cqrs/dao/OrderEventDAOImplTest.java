package fr.soat.cqrs.dao;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.event.OrderDeletedEvent;
import fr.soat.cqrs.event.OrderEvent;
import fr.soat.cqrs.event.OrderSavedEvent;
import fr.soat.cqrs.model.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.*;
import static fr.soat.cqrs.model.order.OrderFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class OrderEventDAOImplTest {

    @Autowired
    OrderEventDAO orderEventDAO;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("TRUNCATE order_event;");
    }

    @Test
    public void should_push_then_pop_order_event() {
        // Given
        Order firstOrder = buildOrder(
                one(TSHIRT_BOB_LEPONGE),
                two(ROBE_REINE_DES_NEIGES)
        );
        Order secondOrder = buildOrder(
                three(CHAUSSETTES_SPIDERMAN),
                one(ROBE_REINE_DES_NEIGES)
        );
        OrderSavedEvent firstEvent = new OrderSavedEvent(firstOrder);
        OrderSavedEvent secondEvent = new OrderSavedEvent(secondOrder);
        OrderDeletedEvent thirdEvent = new OrderDeletedEvent(firstOrder);
        OrderDeletedEvent fourthEvent = new OrderDeletedEvent(secondOrder);

        // When
        orderEventDAO.push(firstEvent);
        orderEventDAO.push(secondEvent);
        orderEventDAO.push(thirdEvent);
        orderEventDAO.push(fourthEvent);

        // Then
        OrderEvent firstPopedEvent = orderEventDAO.pop().get();
        assertThat(firstPopedEvent).isInstanceOf(OrderSavedEvent.class);
        assertThat(firstPopedEvent.getOrder()).isEqualTo(firstOrder);

        OrderEvent secondPopedEvent = orderEventDAO.pop().get();
        assertThat(secondPopedEvent).isInstanceOf(OrderSavedEvent.class);
        assertThat(secondPopedEvent.getOrder()).isEqualTo(secondOrder);

        OrderEvent thirdPopedEvent = orderEventDAO.pop().get();
        assertThat(thirdPopedEvent).isInstanceOf(OrderDeletedEvent.class);
        assertThat(thirdPopedEvent.getOrder()).isEqualTo(firstOrder);

        OrderEvent fourthPopedEvent = orderEventDAO.pop().get();
        assertThat(fourthPopedEvent).isInstanceOf(OrderDeletedEvent.class);
        assertThat(fourthPopedEvent.getOrder()).isEqualTo(secondOrder);
    }

}