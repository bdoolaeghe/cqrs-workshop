package fr.soat.cqrs.dao;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.model.Product;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class ProductDAOImplTest {

    @Autowired
    ProductDAO productDAO;

    @Test
    public void should_get_product_by_ref() {
        Product product = productDAO.getByReference(1L);
        Assert.assertNotNull(product);
    }
}
