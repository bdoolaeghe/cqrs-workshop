package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.order.OrderFixtures;
import fr.soat.cqrs.service.front.FrontService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.*;
import static fr.soat.cqrs.model.order.OrderFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class BackOfficeServiceImplTest {

    @Autowired
    private BackOfficeService backOfficeService;
    @Autowired
    private FrontService frontService;
    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        // We need to clean the DB, because the previous test run commited some data
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("TRUNCATE product_margin;");
        jdbcTemplate.update("TRUNCATE product_order CASCADE;");
    }

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

    @Test
    public void should_decrease_product_margin_when_cancelling_an_order() throws InterruptedException {
        // When
        Long orderId = somebodyOrders(
                one(TSHIRT_BOB_LEPONGE),
                two(ROBE_REINE_DES_NEIGES)
        );

        // Then
        waitAWhile();
        BestSales bestSales = backOfficeService.getBestSales();
        assertThat(firstProduct(bestSales)).isEqualTo(TSHIRT_BOB_LEPONGE.name);
        assertThat(secondProduct(bestSales)).isEqualTo(ROBE_REINE_DES_NEIGES.name);

        // When
        somebodyCancelOrders(orderId);

        // Then
        waitAWhile();
        bestSales = backOfficeService.getBestSales();
        assertThat(bestSales.getSales())
                .extracting(sales -> tuple(sales.getProductName(), sales.getProductMargin()))
                .containsExactlyInAnyOrder(
                        tuple(TSHIRT_BOB_LEPONGE.name, 0f),
                        tuple(ROBE_REINE_DES_NEIGES.name, 0f)
                );
    }

    @Test
    public void should_successfully_order_then_cancel_and_get_consistent_product_margins() throws InterruptedException {
        // When
        for (int i = 0 ; i < 100 ; i++) {
            // save new order
            Long orderId = somebodyOrders(
                    one(TSHIRT_BOB_LEPONGE),
                    two(ROBE_REINE_DES_NEIGES)
            );

            // cancel order
            somebodyCancelOrders(orderId);
        }

        waitAWhile();
        BestSales bestSales = backOfficeService.getBestSales();
        assertThat(bestSales.getSales())
                .extracting(sales -> tuple(sales.getProductName(), sales.getProductMargin()))
                .containsExactlyInAnyOrder(
                        tuple(TSHIRT_BOB_LEPONGE.name, 0f),
                        tuple(ROBE_REINE_DES_NEIGES.name, 0f)
                );
    }




    private void waitAWhile() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    private Long somebodyOrders(List... orderDescription) {
        Order order = OrderFixtures.buildOrder(orderDescription);
        return frontService.order(order);
    }

    private void somebodyCancelOrders(Long orderId) {
        frontService.cancelOrder(orderId);
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
