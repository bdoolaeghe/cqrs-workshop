package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.Order;

public interface OrderDAO {

    Order getById(Long orderId);

    Long insert(Order order);
}
