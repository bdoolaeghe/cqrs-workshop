package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.Product;

public interface ProductDAO {

    Product getByReference(Long reference);

}
