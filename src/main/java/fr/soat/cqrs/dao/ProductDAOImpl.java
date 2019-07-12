package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ProductDAOImpl implements ProductDAO {

    JdbcTemplate jdbcTemplate;

    private final String SQL_FIND_BY_REF = "select * from product where reference = ?";

    @Autowired
    public ProductDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Product getByReference(Long reference) {
        return jdbcTemplate.queryForObject(
                "select * " +
                "from product " +
                "where reference = ?",
                new Object[] { reference }, new ProductMapper());
    }

    @Override
    public BestSales getBestSales() {
        return jdbcTemplate.queryForObject(
                "SELECT product.name as product_name, " +
                        "SUM((price - supply_price) * quantity) AS product_margin  " +
                        "FROM product JOIN order_line ON product.reference = order_line.reference " +
                        "GROUP BY product.name " +
                        "ORDER BY product_margin DESC " +
                        "LIMIT 3",
                new Object[0], new BestSalesMapper());
    }
}
