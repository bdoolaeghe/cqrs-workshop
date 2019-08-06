package fr.soat.cqrs;

import fr.soat.cqrs.configuration.AppConfig;
import fr.soat.cqrs.service.backoffice.ProductMarginUpdater;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// A sample daemon app, logging the incoming updates in table product_margin
public class Main {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        ProductMarginUpdater productMarginUpdater = (ProductMarginUpdater) ctx.getBean("productMarginUpdater");
        productMarginUpdater.start();
        while(true) {
            Thread.sleep(60000);
            System.out.println("Running...");
        }
    }

}
