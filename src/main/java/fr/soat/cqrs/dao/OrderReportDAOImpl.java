package fr.soat.cqrs.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDate;

@Component
public class OrderReportDAOImpl implements OrderReportDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderReportDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void upsert(Long productReference, String productName, float price, LocalDate orderDate) {
            String upsertSQL = "INSERT INTO order_report (product_reference, product_name, price, last_order_date)" +
                    " VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT (product_reference) DO UPDATE " +
                    "SET last_order_date = ? " +
                    "WHERE order_report.product_reference = ?";
            jdbcTemplate.update(upsertSQL, productReference, productName, price, LocalDate.now(), LocalDate.now(), productReference);
        }
}
