package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.BestSales;

public interface ProductMarginDAO {

    BestSales getBestSales();

    void incrementProductMargin(Long productReference, String productName, float marginToAdd);

    void decrementProductMargin(Long productReference, String productName, float marginToRemove) ;
}
