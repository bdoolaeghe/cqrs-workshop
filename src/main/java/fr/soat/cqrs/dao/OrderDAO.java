package fr.soat.cqrs.dao;

import fr.soat.cqrs.model.Order;

import java.util.List;

public interface OrderDAO {

    List<Order> getOrders();

    Order getById(Long orderId);

    Long insert(Order order);

    void delete(Long orderId);
}
