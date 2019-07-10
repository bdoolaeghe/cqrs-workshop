package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper implements RowMapper<Product> {

        public Product mapRow(ResultSet resultSet, int i) throws SQLException {
            Product product = new Product();
            product.setReference(resultSet.getLong("reference"));
            product.setName(resultSet.getString("name"));
            product.setPrice(resultSet.getFloat("price"));
            product.setSupplyPrice(resultSet.getFloat("supply_price"));
            return product;
        }
    }
