package fr.soat.cqrs.dao;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.event.OrderDeletedEvent;
import fr.soat.cqrs.event.OrderEvent;
import fr.soat.cqrs.event.OrderSavedEvent;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.order.OrderFixtures;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.*;
import static fr.soat.cqrs.model.order.OrderFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class OrderEventDAOImplTest {

    @Autowired
    OrderEventDAO orderEventDAO;

    @Test
    @Transactional
    @Rollback(true)
    public void should_push_then_pop_order_event() {
        // Given
        Order firstOrder = OrderFixtures.buildOrder(
                one(TSHIRT_BOB_LEPONGE),
                two(ROBE_REINE_DES_NEIGES)
        );
        Order secondOrder = OrderFixtures.buildOrder(
                three(CHAUSSETTES_SPIDERMAN),
                one(ROBE_REINE_DES_NEIGES)
        );
        OrderSavedEvent firstEvent = new OrderSavedEvent(firstOrder);
        OrderSavedEvent secondEvent = new OrderSavedEvent(secondOrder);
        OrderDeletedEvent thirdEvent = new OrderDeletedEvent(firstOrder);
        OrderDeletedEvent fourthEvent = new OrderDeletedEvent(secondOrder);

        // When
        orderEventDAO.pushOrderEvent(firstEvent);
        orderEventDAO.pushOrderEvent(secondEvent);
        orderEventDAO.pushOrderEvent(thirdEvent);
        orderEventDAO.pushOrderEvent(fourthEvent);

        // Then
        OrderEvent firstPopedEvent = orderEventDAO.popOrderEvent();
        assertThat(firstPopedEvent).isInstanceOf(OrderSavedEvent.class);
        assertThat(firstPopedEvent.getOrder()).isEqualTo(firstOrder);

        OrderEvent secondPopedEvent = orderEventDAO.popOrderEvent();
        assertThat(secondPopedEvent).isInstanceOf(OrderSavedEvent.class);
        assertThat(secondPopedEvent.getOrder()).isEqualTo(secondOrder);

        OrderEvent thirdPopedEvent = orderEventDAO.popOrderEvent();
        assertThat(thirdPopedEvent).isInstanceOf(OrderDeletedEvent.class);
        assertThat(thirdPopedEvent.getOrder()).isEqualTo(firstOrder);

        OrderEvent fourthPopedEvent = orderEventDAO.popOrderEvent();
        assertThat(fourthPopedEvent).isInstanceOf(OrderDeletedEvent.class);
        assertThat(fourthPopedEvent.getOrder()).isEqualTo(secondOrder);

    }

}