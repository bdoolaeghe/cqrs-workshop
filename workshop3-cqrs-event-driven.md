# Workshop 3: getBestSales() in a CQRS way and event driven

_Goal:_ 
Enhance the previous workshop2 getBestSales() CQRS solution to avoid service coupling, thanks to an event bus.

## Setup a synchronous event bus
As a simple solution, we'll use the [spring rfamework event bus](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationEventPublisher.html) implementation.

### Publish event when write model is updated
In the `OrderDAOIMpl`, autowire the `ApplicationEventPublisher` spring bean :
```
@Service
public class OrderDAOImpl implements ORderDAO {
    ...
    @Autowired
    private final ApplicationEventPublisher eventPublisher;
    ...
}
```
Now, the goal is to publish an event after having saved the order in DB. 
* create an event class `OrderSavedEvent`, containing the saved order.
* publish an instance of `OrderSavedEvent` :
```
    @Override
    public Long insert(Order order) {
        // save
        Long orderId = insertOrder(order);
        order.setId(orderId);
        insertLines(orderId, order.getLines());

        // notiffy
        eventPublisher.publishEvent(new OrderSavedEvent(order));
        return orderId;
    }
```
*You can check this [tutorial](https://www.baeldung.com/spring-events#publisher) for more details.*


### Consume event to update best sales read model
Now, we need to create a new event listener, to consume that event and update the `product_margin` table.

* create a new spring bean `ProductMarginUpdater`, with an `@EvnetListener` annotated `onOrderSavedEvent(OrderSavedEvent orderSavedEvent)` method:
```
@Service
public class ProductMarginUpdater {

    @EventListener
    public void onOrderSavedEvent(OrderSavedEvent orderSavedEvent) {
        ...
    }
}
```
*You can check this [tutorial](https://www.baeldung.com/spring-events#annotation-driven) for more details*

* implement the `onOrderSavedEvent(OrderSavedEvent orderSavedEvent)` method to compute the product margin and save it into the `product_margin` table.

N.B.: by default, the publish and consume operations are synchronously executed by spring, and will happen in the same transaction.
