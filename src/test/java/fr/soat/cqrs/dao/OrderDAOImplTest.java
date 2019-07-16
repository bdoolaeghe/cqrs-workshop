package fr.soat.cqrs.dao;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.order.OrderFixtures;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.ROBE_REINE_DES_NEIGES;
import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.TSHIRT_BOB_LEPONGE;
import static fr.soat.cqrs.model.order.OrderFixtures.one;
import static fr.soat.cqrs.model.order.OrderFixtures.two;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class OrderDAOImplTest {

    @Autowired
    OrderDAO orderDAO;

    @Test
    @Rollback(true)
    public void should_save_and_load_order_by_id() {
        // Given
        Order order = OrderFixtures.buildOrder(
                one(TSHIRT_BOB_LEPONGE),
                two(ROBE_REINE_DES_NEIGES)
        );
        Long id = orderDAO.insert(order);

        // When
        Order reloadedOrder = orderDAO.getById(id);

        // then
        assertThat(asJson(reloadedOrder)).isEqualTo(asJson(order));
    }

    public static final Gson gson = new GsonBuilder()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getDeclaredClass().equals(Long.class)
                            && f.getName().equals("id");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .setPrettyPrinting().create();

    private static String asJson(Object o) {
        return gson.toJson(o);
    }
}
