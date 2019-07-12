package fr.soat.cqrs.dao;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.OrderLine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class OrderDAOImplTest {

    @Autowired
    OrderDAO orderDAO;

    @Test
    public void should_get_order_by_id() {
        Order order = orderDAO.getById(101L);
        Assert.assertNotNull(order);
        assertThat(order.getLines().size()).isEqualTo(2);
        assertThat(order.getLines().get(0).getQuantity()).isEqualTo(2);
        assertThat(order.getLines().get(1).getQuantity()).isEqualTo(1);
    }

    @Test
    @Rollback(true)
    public void should_save_new_order() {
        Order order = newOrderWith2Lines();
        Long orderId = orderDAO.insert(order);

        Order reloadedOrder = orderDAO.getById(orderId);

        Assert.assertNotNull(reloadedOrder);
//        assertThat(reloadedOrder).isEqualTo(order);
    }

    private Order newOrderWith2Lines() {
        Order order = new Order();
        order.getLines()
                .add(OrderLine.builder()
                .productReference(1L)
                .quantity(3)
                .build());
        order.getLines().add(OrderLine.builder()
                .productReference(2L)
                .quantity(5)
                .build());
        return order;
    }
}
