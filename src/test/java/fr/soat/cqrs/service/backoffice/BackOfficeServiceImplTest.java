package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.order.OrderFixtures;
import fr.soat.cqrs.service.front.FrontService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.*;
import static fr.soat.cqrs.model.order.OrderFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class BackOfficeServiceImplTest {

    @Autowired
    private BackOfficeService backOfficeService;
    @Autowired
    private FrontService frontService;

    @Test
    public void should_find_best_sales() {
        // Given
        somebodyOrders(
                one(TSHIRT_BOB_LEPONGE),
                two(ROBE_REINE_DES_NEIGES)
                );
        somebodyOrders(
                two(TSHIRT_BOB_LEPONGE),
                three(CHAUSSETTES_SPIDERMAN)
                );

        // When
        BestSales bestSales = backOfficeService.getBestSales();

        // Then
        assertThat(firstProduct(bestSales)).isEqualTo(TSHIRT_BOB_LEPONGE.name);
        assertThat(secondProduct(bestSales)).isEqualTo(ROBE_REINE_DES_NEIGES.name);
        assertThat(tThirdProduct(bestSales)).isEqualTo(CHAUSSETTES_SPIDERMAN.name);
    }

    private void somebodyOrders(List... orderDescription) {
        Order order = OrderFixtures.buildOrder(orderDescription);
        frontService.order(order);
    }

    public String firstProduct(BestSales bestSales) {
        return bestSales.getSales().get(0).getProductName();
    }

    public String secondProduct(BestSales bestSales) {
        return bestSales.getSales().get(1).getProductName();
    }

    public String tThirdProduct(BestSales bestSales) {
        return bestSales.getSales().get(2).getProductName();
    }


}
