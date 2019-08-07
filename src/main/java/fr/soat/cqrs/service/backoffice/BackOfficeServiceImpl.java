package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.dao.OrderDAO;
import fr.soat.cqrs.dao.OrderReportDAO;
import fr.soat.cqrs.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackOfficeServiceImpl implements BackOfficeService {

    private final OrderDAO orderDAO;
    private final OrderReportDAO orderReportDAO;

    @Autowired
    public BackOfficeServiceImpl(OrderDAO orderDAO, OrderReportDAO orderReportDAO) {
        this.orderDAO = orderDAO;
        this.orderReportDAO = orderReportDAO;
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderDAO.getById(orderId);
    }

    @Override
    public List<String> getOrderReport() {
        return orderReportDAO.getAllSortByDate();
    }

}
