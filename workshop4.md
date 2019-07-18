# Workshop 4: getBestSales() in a CQRS way and event driven async

_Goal:_ 
Enhance the previous workshop3 getBestSales() CQRS event driven solution with async event consumer.

## Enable async event listener 
By default, the event bus publisher and subscribers are executed synchronously (in sequence on the same thread). We can make subscribers asynchronously executed (in a separate thread).
First of all, enable the async spring support, by adding `@EnableAsync` onto a spring `@Configuration` class:
```
@Configuration
...
@EnableAsync
public class AppConfig {
   ...
}
``` 
Now, we just need to add the `@Async` annotation onto the subscriber `@EventListener` decorated method:
```
public class ProductMarginUpdater {
    ...
    @Async
    @Transactional
    @EventListener
    public void onOrderSavedEvent(OrderSavedEvent orderSavedEvent) {
       ...
    }
}
```
By this way, the subscriber will be invoked in a dedicated thread, and won't block the execution of `FrontService.order()`.

*N.B.: You can check this [tutorial](https://www.baeldung.com/spring-events#annotation-driven) for more details on `@Async`*
*N.B.: `onOrderSavedEvent()` is now `@Transactional` decorated, because the async consumer works now in a different transactional context from the publisher one.*


## Test it !

Now the `BackOfficeServiceImplTest` is failing ! Any idea why ? 
Many issues we have in async world...

### Data are *only* eventually readable in `product_margin` table 
The first issue we have, is that the `product_margin` is no more consistent with `product_order`: there is a delay to update `product_margin` due to async update from subscriber.
In the test context, the `main` thread running the test, is saving the `product_order`, and then check the `getBestSales()`... But the subscriber thread may have not finished to update the `product_margin` table yet !!!
A "simple" fix could be to sleep a while in the test, before calling the `getBestSales()`:
```
// save order
TimeUnit.SECONDES.sleep(1);
// check getBestSales()
```
All good now ?

### Concurrent duplicate insert issue
You probably also should have another issue with that test:
```
ERROR org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler - Unexpected exception occurred invoking async method: public void fr.soat.cqrs.service.backoffice.ProductMarginUpdater.onOrderSavedEvent(fr.soat.cqrs.event.OrderSavedEvent)
org.springframework.dao.DuplicateKeyException: PreparedStatementCallback; SQL [INSERT INTO product_margin (product_reference, product_name, total_margin) VALUES (?, ?, ?)]; ERROR: duplicate key value violates unique constraint "product_margin_pkey"
  Détail : Key (product_reference)=(2) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "product_margin_pkey"
  Détail : Key (product_reference)=(2) already exists.
```
What is happening ? As the `ProductMarginUpdater` is invoked in a separate thread, when we save sequentially 2 orders, we trigger 2 concurrent `ProductMarginUpdater` executions (one per product order). Unfortunately, our implementation is not protected against concurrent modifications:
```
   public void incrementProductMargin(Long productReference, String productName, float marginToAdd) {
        // increment total_margin of product
        ...
        int updated = jdbcTemplate.update(...);

        // if product not sold yet, we need to init its total_margin
        if (updated == 0) {
            ...
            jdbcTemplate.insert(...);
        }
    }
```  
That implementation was fine in sync world, but fails in concurrent context: if 2 concurrent updates occurs (and return 0 updated rows when the product margin is not found), we have 2 concurrent insert attempt for the same product ! (failure) 
To make it work, we need to protect against concurrent invocations. Different ways to do that, but the simplest one is to let the DB manage the concurrent writes, by applying one atomic upsert instead of an update followed by an insert:
```
    @Override
    public void incrementProductMargin(Long productReference, String productName, float marginToAdd) {
        // try to insert initial total_margin. If already exists, increment total_margin
        String upsertSQL =
                "INSERT INTO product_margin (product_reference, product_name, total_margin)" +
                " VALUES (?, ?, ?) " +
                "ON CONFLICT (product_reference) DO UPDATE " +
                "SET total_margin = product_margin.total_margin + ? " +
                "WHERE product_margin.product_reference = ?";
            jdbcTemplate.update(upsertSQL, productReference, productName, marginToAdd, marginToAdd, productReference);
    }
```
*check the postegres documentation on [upsert statments](http://www.postgresqltutorial.com/postgresql-upsert/)* 

### Inconsistency due to different transactional contexts
What will happen if the `product_order` table update fails ? The event has been raised and consumed to update the `product_margin`
il faut utiliser le AFTER_COMMIT

### Inconsistency due to event disordering
use 1-thread thread pool

*
