package fr.soat.cqrs.model.order;

import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.OrderLine;
import lombok.AllArgsConstructor;
import org.assertj.core.util.Lists;

import java.util.List;

public class OrderFixtures {
    public static Order buildOrder(List... products) {
        Order order = new Order();
        for (List pair : products) {
            Long reference = (Long) pair.get(0);
            int quantity = (Integer) pair.get(1);
            order.getLines().add(OrderLine.builder()
                    .quantity(quantity)
                    .productReference(reference)
                    .build());
        }
        return order;
    }

    public static  List<?> two(ProductEnum product) {
        return Lists.newArrayList(product.reference, 2);
    }

    public static  List<?> three(ProductEnum product) {
        return Lists.newArrayList(product.reference, 3);
    }

    public static List<?> one(ProductEnum product) {
        return Lists.newArrayList(product.reference, 1);
    }

    @AllArgsConstructor
    public
    enum ProductEnum {
        CHAUSSETTES_SPIDERMAN(1L, "Chaussettes spiderman"),
        TSHIRT_BOB_LEPONGE(2L, "t-shirt bob l eponge"),
        ROBE_REINE_DES_NEIGES(3L, "robe reine des neiges");
        public final Long reference;
        public final String name;
    }
}
