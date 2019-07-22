package fr.soat.cqrs.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ProductInventoryDAOImpl implements ProductInventoryDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductInventoryDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void increaseProductInventory(Long productReference, int provisionedQuantity) {
        // try to insert initial quantity. If already exists, increment quantity
        //FIXME
        throw new RuntimeException("implement me !");
    }

    @Override
    public void decreaseProductInventory(Long productReference, int removedQuantity) {
        // 3 cases:
        // * decrease succeed
        // * decrease failed because new quantity is < 0 (DB constraint violation)
        // * decrease failed because no stock (no row for the product reference
        //FIXME
        throw new RuntimeException("implement me !");
    }

    @Override
    public Integer getStock(Long productReference) {
        return jdbcTemplate.queryForObject(
                "select quantity " +
                        "from product_inventory " +
                        "where product_reference = ?",
                new Object[] { productReference }, (rs, rowNum) -> rs.getInt("quantity"));
    }
}
