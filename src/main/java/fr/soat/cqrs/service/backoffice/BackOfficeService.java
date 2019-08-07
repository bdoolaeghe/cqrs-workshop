package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.model.Order;

import java.util.List;

public interface BackOfficeService {

    Order getOrder(Long orderId);

    List<String> getOrderReport();

}
