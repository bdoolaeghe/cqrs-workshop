# Workshop 6: async event subscriber with ordering guarantee

_Goal:_ 
Guarantee the event consumption order (same as publish order)

## Implement cancelOrder()

First, we want to implement the new feature `FrontService.cancelOrder(orderId)`, to cancel a previously saved order.
This service should:
* delete the order from `product_order` and each order line in `order_line` for the corresponding order
* update `product_inventory`, to reincrease the remaining quantity

Implement the service `FrontService.cancelOrder()` and `OrderDAO.delete(orderiD)`. This DAO method should raise a `OrderDeletedEvent`, that a new event listener method `onDeletedOrderEvent()` should handle in `ProductMarginUpdater`, to update `product_margin` and decrease the order corresponding margins.
Once implemented, `BackOfficeServiceImplTest.should_decrease_product_margin_when_cancelling_an_order()` should pass green.


## What could possibly go wrong ?

Execute now the test `BackOfficeServiceImplTest.should_successfully_order_then_cancel_and_get_consistent_product_margins()`. What's going on ? check logs to understand why the test fails.

To fix the issue, we shall consume the `OrderSavedEvent` and the `OrderDeletedEvent` asynchronously, but sequentially, to avoid breaking the event order. Declare a 1-thread thread pool in `AppConfig`:
```
@Configuration
public class AppConfig {
   ...
   @Bean
   public Executor productMarginUpdaterThreadPool() {
       return Executors.newFixedThreadPool(1, task -> new Thread(task, "product-margin-updater-thread"));
   }
   ...
}
```
Then, configure the `@Async` event listener methods `onOrderSavedEvent()` and `onOrderDeletedEvent()` to share the same thread through this thread pool:
```

@Service
public class ProductMarginUpdater {

    @Async("productMarginUpdaterThreadPool")
    @Transactional
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onOrderSavedEvent(OrderSavedEvent orderSavedEvent) {
       ...
    }

    @Async("productMarginUpdaterThreadPool")
    @Transactional
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onOrderDeletedEvent(OrderDeletedEvent orderDeletedEvent) {
        
        ...
    }
    
}
```

Check now `BackOfficeServiceImplTest.should_successfully_order_then_cancel_and_get_consistent_product_margins()` pass green.

*N.B.: check this [tutorial](https://www.baeldung.com/spring-async#override-the-executor-at-the-method-level) for more details about executing `@Async` method on dedicated thread pool*
