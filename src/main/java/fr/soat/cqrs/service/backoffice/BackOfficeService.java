package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.Product;

import java.util.List;

public interface BackOfficeService {

    Order getOrder(Long orderId);

    List<Product> getBestSales();

}
