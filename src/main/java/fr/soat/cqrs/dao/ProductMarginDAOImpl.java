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
        //FIXME
        throw new RuntimeException("implement workshop2 !");
    }

    @Override
    public void incrementProductMargin(Long productReference, String productName, float marginToAdd) {
        //FIXME
        throw new RuntimeException("implement workshop2 !");
    }
}
