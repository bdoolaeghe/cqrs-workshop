package fr.soat.cqrs.service;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.order.OrderFixtures;
import fr.soat.cqrs.service.backoffice.BackOfficeService;
import fr.soat.cqrs.service.backoffice.OrderReportUpdater;
import fr.soat.cqrs.service.front.FrontService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.CHAUSSETTES_SPIDERMAN;
import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.TSHIRT_BOB_LEPONGE;
import static fr.soat.cqrs.model.order.OrderFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class BackOfficeServiceImplTest {

    @Autowired
    private BackOfficeService backOfficeService;
    @Autowired
    private FrontService frontService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private OrderReportUpdater orderReportUpdater;

    @BeforeClass
    public static void beforeClass() {
        cleanConnectorOffset();
    }

    @Before
    public void setUp() {
        // We need to clean the DB, because the previous test run commited some data
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("TRUNCATE order_report;");
        jdbcTemplate.update("TRUNCATE product_order CASCADE;");
        log.info("DB cleaned");

        orderReportUpdater.start();
    }

    private static void cleanConnectorOffset() {
        // we use kafka connect offset files backing store in /tmp/debezium for tests
        // to let debezium store the current WAL offset of "captured" changes

        // delete and recreate offset files folder
        try {
            File offsetDir = new File("/tmp/debezium");
            FileUtils.deleteDirectory(offsetDir);
            offsetDir.mkdir();
        } catch (IOException e) {
            log.warn("Failed to clean offset files", e);
        }
    }

    @Test
    public void should_load_order_report_after_orders() {
        // Given
        somebodyOrders(
                one(TSHIRT_BOB_LEPONGE)
        );
        somebodyOrders(
                two(TSHIRT_BOB_LEPONGE),
                three(CHAUSSETTES_SPIDERMAN)
        );

        // When
        waitAWhile();
        List<String> report = backOfficeService.getOrderReport();

        // Then
        assertThat(report.size()).isEqualTo(2);
        assertThat(report.get(0)).isEqualTo("2;t-shirt bob l eponge;5.9;2019-08-07;");
        assertThat(report.get(1)).isEqualTo("1;Chaussettes spiderman;2.9;2019-08-07;");
    }



    private void waitAWhile(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitAWhile() {
        waitAWhile(1);
    }

    private Long somebodyOrders(List... orderDescription) {
        Order order = OrderFixtures.buildOrder(orderDescription);
        return frontService.order(order);
    }


}
