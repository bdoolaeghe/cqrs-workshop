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


## Remaining design issues with async

### Inconsistency due to distinct transactions
What will happen if the `product_order` table update fails ? whatever, the event has been raised and consumed by `ProductMarginUpdater`... That means:
* order is not saved in `product_order`...
* but `total_margin` has been increased in `product_margin` !

It is impossible de regroup `product_order` and `product_margin` updates in a same transaction, due to async... 
BUT, we can choose to raise the event only if the `product_order` update commit succeed. To apply that strategy, annotate the `onOrderSavedEvent()` method  with `@TransactionalEventListener` instead of `@EventListener`. 
This will invoke the listener callback just after the commit of transaction made by the publisher: 
````
@Service
public class ProductMarginUpdater {
 
    @Async
    @Transactional
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onOrderSavedEvent(OrderSavedEvent orderSavedEvent) {
       ...
    }
}
````
N.B.; `@TransactionalEventListener` means the callback invocation depends on the *publisher* transaction state. The `@Transactional` means the *subscriber* callback will occur in a new transaction...


### Inconsistency due to event disordering
use 1-thread thread pool

*
