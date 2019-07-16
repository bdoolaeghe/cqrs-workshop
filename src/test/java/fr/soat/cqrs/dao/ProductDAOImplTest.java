package fr.soat.cqrs.dao;

import fr.soat.cqrs.configuration.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static fr.soat.cqrs.model.order.OrderFixtures.ProductEnum.*;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class ProductDAOImplTest {

    @Autowired
    ProductDAO productDAO;

    @Test
    public void should_get_product_by_ref() {
        assertNotNull(productDAO.getByReference(CHAUSSETTES_SPIDERMAN.reference));
        assertNotNull(productDAO.getByReference(ROBE_REINE_DES_NEIGES.reference));
        assertNotNull(productDAO.getByReference(TSHIRT_BOB_LEPONGE.reference));
    }
}
