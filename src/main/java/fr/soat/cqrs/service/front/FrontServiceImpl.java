package fr.soat.cqrs.service.front;

import fr.soat.cqrs.model.Order;
import org.springframework.stereotype.Service;

@Service
public class FrontServiceImpl implements FrontService {

    @java.lang.Override
    public void order(Order order) {
        throw new RuntimeException("implement me !");
    }
}
