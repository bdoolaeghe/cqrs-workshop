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

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.*;
import static fr.soat.cqrs.model.order.OrderFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(firstProductMargin(bestSales)).isEqualTo(12f);
        assertThat(secondProduct(bestSales)).isEqualTo(ROBE_REINE_DES_NEIGES.name);
        assertThat(secondProductMargin(bestSales)).isEqualTo(4f);
        assertThat(tThirdProduct(bestSales)).isEqualTo(CHAUSSETTES_SPIDERMAN.name);
        assertThat(thirdProductMargin(bestSales)).isEqualTo(3f);
    }

    private void somebodyOrders(List... orderDescription) {
        Order order = OrderFixtures.buildOrder(orderDescription);
        frontService.order(order);
    }

    public String firstProduct(BestSales bestSales) {
        return bestSales.getSales().get(0).getProductName();
    }

    public Float firstProductMargin(BestSales bestSales) {
        return bestSales.getSales().get(0).getProductMargin();
    }

    public String secondProduct(BestSales bestSales) {
        return bestSales.getSales().get(1).getProductName();
    }

    public Float secondProductMargin(BestSales bestSales) {
        return bestSales.getSales().get(1).getProductMargin();
    }


    public String tThirdProduct(BestSales bestSales) {
        return bestSales.getSales().get(2).getProductName();
    }

    public Float thirdProductMargin(BestSales bestSales) {
        return bestSales.getSales().get(2).getProductMargin();
    }


}
