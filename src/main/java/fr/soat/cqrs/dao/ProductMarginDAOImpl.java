package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.BestSales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.dao.EmptyResultDataAccessException;

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
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT product_name, " +
                            " total_margin as product_margin " +
                            "FROM product_margin " +
                            "ORDER BY total_margin DESC " +
                            "LIMIT 3",
                    new Object[0], new BestSalesMapper());
        } catch (EmptyResultDataAccessException e) {
            return new BestSales();
        }    }

    @Override
    public void incrementProductMargin(Long productReference, String productName, float marginToAdd) {
        // try to insert initial total_margin. If already exists, increment total_margin
        String upsertSQL = "INSERT INTO product_margin (product_reference, product_name, total_margin)" +
                " VALUES (?, ?, ?) " +
                "ON CONFLICT (product_reference) DO UPDATE " +
                "SET total_margin = product_margin.total_margin + ? " +
                "WHERE product_margin.product_reference = ?";
        jdbcTemplate.update(upsertSQL, productReference, productName, marginToAdd, marginToAdd, productReference);
    }
}

