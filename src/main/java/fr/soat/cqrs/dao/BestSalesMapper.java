package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Sales;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BestSalesMapper implements RowMapper<BestSales> {
    @Override
    public BestSales mapRow(ResultSet resultSet, int i) throws SQLException {
        BestSales bestSales = new BestSales();
        do {
            Sales sales = new Sales(
                    resultSet.getString("product_name"),
                    resultSet.getFloat("product_margin")
            );
            bestSales.getSales().add(sales);
        } while (resultSet.next());
        return bestSales;
    }
}
