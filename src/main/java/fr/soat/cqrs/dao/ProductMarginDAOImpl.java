package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.BestSales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ProductMarginDAOImpl implements ProductMarginDAO {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductMarginDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public BestSales getBestSales() {
        return jdbcTemplate.queryForObject(
                "SELECT product_name, " +
                        " total_margin as product_margin " +
                        "FROM product_margin "+
                        "ORDER BY total_margin DESC " +
                        "LIMIT 3",
                new Object[0], new BestSalesMapper());
    }

    @Override
    public void incrementProductMargin(Long productReference, String productName, float marginToAdd) {
        // increment total_margin of product
        String updateSql =
                "UPDATE product_margin " +
                "SET total_margin = total_margin + ? " +
                "WHERE product_reference = ?";
        int updated = jdbcTemplate.update(updateSql, marginToAdd, productReference);

        // if product not sold yet, we need to init its total_margin
        if (updated == 0) {
            String insertSQL = "INSERT INTO product_margin (product_reference, product_name, total_margin) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertSQL, productReference, productName, marginToAdd);
        }
    }
}
