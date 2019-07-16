package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ProductDAOImpl implements ProductDAO {

    JdbcTemplate jdbcTemplate;

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

}
