package fr.soat.cqrs.service.backoffice;

import fr.soat.cqrs.model.Order;
import fr.soat.cqrs.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackOfficeServiceImpl implements BackOfficeService {
    @Override
    public Order getOrder(Long orderId) {
        throw new RuntimeException("implement me !");
    }

    @Override
    public List<Product> getBestSales() {
        throw new RuntimeException("implement me !");
    }
}
