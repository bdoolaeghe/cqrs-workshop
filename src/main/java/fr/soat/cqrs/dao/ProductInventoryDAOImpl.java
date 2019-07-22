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
        String upsertSQL = "INSERT INTO product_inventory (product_reference, quantity)" +
                " VALUES (?, ?) " +
                "ON CONFLICT (product_reference) DO UPDATE " +
                "SET quantity = product_inventory.quantity + ? " +
                "WHERE product_inventory.product_reference = ?";
        jdbcTemplate.update(upsertSQL, productReference, provisionedQuantity, provisionedQuantity, productReference);
    }

    @Override
    public void decreaseProductInventory(Long productReference, int removedQuantity) {
        String upsertSQL = "UPDATE product_inventory SET quantity = product_inventory.quantity - ? WHERE product_reference = ?";
        int update = 0;
        try {
            update = jdbcTemplate.update(upsertSQL, removedQuantity, productReference);
        } catch (DataIntegrityViolationException e) {
            // if too many bought quantity regarding to the remaining stock
            if (e.getMessage().contains("product_inventory_quantity_check"))  {
                throw new InventoryException("Stock too low for product " + productReference);
            }
        }

        // if had never stocked the product
        if (update == 0) {
            throw new InventoryException("Empty stock for product " + productReference);
        }
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
