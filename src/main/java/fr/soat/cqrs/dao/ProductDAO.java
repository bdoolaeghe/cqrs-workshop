package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Product;

public interface ProductDAO {

    Product getByReference(Long reference);

    BestSales getBestSales();
}
