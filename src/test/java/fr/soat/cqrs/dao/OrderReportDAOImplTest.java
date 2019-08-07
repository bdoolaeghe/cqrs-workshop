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

        // then
        Integer count = new JdbcTemplate(dataSource).queryForObject("select count(*) from order_report", Integer.class);
        assertThat(count).isEqualTo(2);
    }

}
