package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.model.BestSales;
import fr.soat.cqrs.model.Order;

public interface BackOfficeService {

    Order getOrder(Long orderId);

    BestSales getBestSales();

}
