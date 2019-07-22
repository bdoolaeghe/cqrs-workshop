package fr.soat.cqrs.service.front;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.dao.InventoryException;
import fr.soat.cqrs.dao.OrderDAO;
import fr.soat.cqrs.dao.ProductInventoryDAO;
import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.order.OrderFixtures;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.List;

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.CHAUSSETTES_SPIDERMAN;
import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.TSHIRT_BOB_LEPONGE;
import static fr.soat.cqrs.model.order.OrderFixtures.three;
import static fr.soat.cqrs.model.order.OrderFixtures.two;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class FrontServiceImplTest {

    @Autowired
    private FrontService frontService;
    @Autowired
    private ProductInventoryDAO productInventoryDAO;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        // We need to clean the DB, because the previous test run commited some data
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("TRUNCATE product_margin;");
        jdbcTemplate.update("TRUNCATE product_inventory;");
        jdbcTemplate.update("TRUNCATE product_order CASCADE;");
    }

    @Test
    public void should_decrease_inventory_when_ordering() {
        // Given
        productInventoryDAO.increaseProductInventory(TSHIRT_BOB_LEPONGE.reference, 10);
        productInventoryDAO.increaseProductInventory(CHAUSSETTES_SPIDERMAN.reference, 20);

        // When
        somebodyOrders(
                two(TSHIRT_BOB_LEPONGE),
                three(CHAUSSETTES_SPIDERMAN)
        );

        // Then
        assertThat(orderDAO.getOrders().size()).isEqualTo(1);
        assertThat(productInventoryDAO.getStock(TSHIRT_BOB_LEPONGE.reference)).isEqualTo(8);
        assertThat(productInventoryDAO.getStock(CHAUSSETTES_SPIDERMAN.reference)).isEqualTo(17);
    }

    @Test
    public void should_raise_an_error_when_trying_to_order_a_product_with_too_few_stock() {
        // Given
        productInventoryDAO.increaseProductInventory(TSHIRT_BOB_LEPONGE.reference, 1);

        assertThatThrownBy(() -> {
            // When
            somebodyOrders(
                    two(TSHIRT_BOB_LEPONGE)
            );
        }).isInstanceOf(InventoryException.class).hasMessageContaining("Stock too low");

        // Then no order is saved
        assertThat(orderDAO.getOrders()).isEmpty();
    }

    @Test
    public void should_raise_an_error_when_trying_to_order_a_product_out_of_stock() {
        assertThatThrownBy(() -> {
            // When
            somebodyOrders(
                    two(TSHIRT_BOB_LEPONGE)
            );
        }).isInstanceOf(InventoryException.class).hasMessageContaining("Empty stock");

        // Then no order is saved
        assertThat(orderDAO.getOrders()).isEmpty();
    }


    private Long somebodyOrders(List... orderDescription) {
        Order order = OrderFixtures.buildOrder(orderDescription);
        return frontService.order(order);
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