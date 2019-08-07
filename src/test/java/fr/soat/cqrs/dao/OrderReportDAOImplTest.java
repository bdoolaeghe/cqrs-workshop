package fr.soat.cqrs.dao;

import fr.soat.cqrs.configuration.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.CHAUSSETTES_SPIDERMAN;
import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.TSHIRT_BOB_LEPONGE;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class OrderReportDAOImplTest {

    @Autowired
    private OrderReportDAO orderReportDAO;

    @Autowired
    DataSource dataSource;

    @Test
    @Transactional
    @Rollback(true)
    public void should_save_and_load_order_report() {
        // When
        orderReportDAO.upsert(TSHIRT_BOB_LEPONGE.reference, TSHIRT_BOB_LEPONGE.name, 5.9f, LocalDate.now());
        orderReportDAO.upsert(TSHIRT_BOB_LEPONGE.reference, TSHIRT_BOB_LEPONGE.name, 5.9f, LocalDate.now());
        orderReportDAO.upsert(CHAUSSETTES_SPIDERMAN.reference, CHAUSSETTES_SPIDERMAN.name, 2.9f, LocalDate.now());
        List<String> report = orderReportDAO.getAllSortByDate();

        // then
        assertThat(report.size()).isEqualTo(2);
        assertThat(report.get(0)).isEqualTo("2;t-shirt bob l eponge;5.9;2019-08-07;");
        assertThat(report.get(1)).isEqualTo("1;Chaussettes spiderman;2.9;2019-08-07;");
    }

}
