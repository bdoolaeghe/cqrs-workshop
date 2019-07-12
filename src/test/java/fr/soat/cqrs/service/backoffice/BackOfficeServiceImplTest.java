package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.OrderLine;
import fr.soat.cqrs.service.front.FrontService;
import lombok.AllArgsConstructor;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static fr.soat.cqrs.service.backoffice.BackOfficeServiceImplTest.ProductEnum.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class BackOfficeServiceImplTest {

    @AllArgsConstructor
    enum ProductEnum {
        CHAUSSETTES_SPIDERMAN(1L, "Chaussettes spiderman"),
        TSHIRT_BOB_LEPONGE(2L, "t-shirt bob l eponge"),
        ROBE_REINE_DES_NEIGES(3L, "robe reine des neiges");
        final Long reference;
        final String name;
    }


    @Autowired
    private BackOfficeService backOfficeService;
    @Autowired
    private FrontService frontService;

    @Test
    public void should_find_best_sales() {
        // Given
        someBodyOrders(
                one(TSHIRT_BOB_LEPONGE),
                two(ROBE_REINE_DES_NEIGES)
                );
        someBodyOrders(
                two(TSHIRT_BOB_LEPONGE),
                three(CHAUSSETTES_SPIDERMAN)
                );

        // When
        BestSales bestSales = backOfficeService.getBestSales();

        // Then
        assertThat(getFirstProduct(bestSales)).isEqualTo(TSHIRT_BOB_LEPONGE.name);
        assertThat(getSecondProduct(bestSales)).isEqualTo(ROBE_REINE_DES_NEIGES.name);
        assertThat(getThirdProduct(bestSales)).isEqualTo(CHAUSSETTES_SPIDERMAN.name);
    }

    private void someBodyOrders(List... products) {
        Order order = new Order();
        for (List pair : products) {
            Long reference = (Long) pair.get(0);
            int quantity = (Integer) pair.get(1);
            order.getLines().add(OrderLine.builder()
                    .quantity(quantity)
                    .productReference(reference)
                    .build());
        }
        frontService.order(order);
    }


    public String getFirstProduct(BestSales bestSales) {
        return bestSales.getSales().get(0).getProductName();
    }

    public String getSecondProduct(BestSales bestSales) {
        return bestSales.getSales().get(1).getProductName();
    }

    public String getThirdProduct(BestSales bestSales) {
        return bestSales.getSales().get(2).getProductName();
    }

    private List<?> one(ProductEnum product) {
        return Lists.newArrayList(product.reference, 1);
    }
    private List<?> two(ProductEnum product) {
        return Lists.newArrayList(product.reference, 2);
    }
    private List<?> three(ProductEnum product) {
        return Lists.newArrayList(product.reference, 3);
    }

}
