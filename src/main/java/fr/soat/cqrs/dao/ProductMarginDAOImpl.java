package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.BestSales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
        }
    }

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

    @Override
    public void decrementProductMargin(Long productReference, String productName, float marginToRemove) {
        //FIXME
        // update product_margin to decrease the margin on the product
        // nota: don't forget the error cases:
        // 1) the case where there is no row in product_margin for the product reference: throw new ProductMarginException("No total margin for product " + productReference);
        // 2) the case where the margin becomes negative after the cancel: throw new ProductMarginException("Negative total margin for product " + productReference);
        throw new RuntimeException("implement me !");
    }
}

