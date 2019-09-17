# Workshop 5: consistent async event consumer

_Goal:_ 
Enhance the previous workshop4 async solution with more data consistency

## Implement the inventory management

First of all, we want to add a new feature: when some products are ordered by a customer, the inventory should be updated, to decrease the remaining quantity in stock.
Create a new table `product_inventory` in `src/main/sql/1-schema.sql`
```
CREATE TABLE product_inventory
(
    product_reference INTEGER PRIMARY KEY,
    quantity      INTEGER CHECK (quantity >= 0)
);
```
*N.B.: we add a constraint to make sure the quantity in inventory remains positive.*

Update the DB to apply changes:
```
cqrs-workshop/> make db/reset
```

Then, add in the `FrontServiceImpl.order()` service method a call to a new `ProductInventoryDAO` to decrease the quantity in `product_inventory`. 
Implement `ProductInventoryDAOImpl` until `FrontServiceImplTest` is green ! 

*N.B.: Don't forget, the `product_order` should not be saved when the `product_inventory` is empty or too low, regarding to the order quantity*


## Consistency issue with async consumer

The test `BackOfficeServiceImplTest.should_not_update_best_sales_when_order_is_refused_due_to_low_inventory()` is failing. What is happening ?
We try to order with too low quantity in inventory. As expected in this case, the transaction is rolled back:
* order is not saved in `product_order`
* inventory is not decreased in `product_inventory`

... BUT an `OrderSAvedEvent` is raised and async consumed by the `ProductMarginUpdater`, who updates the best sales in table `product_margin`, leading to inconsistency between order in `product_order` and `product_margin` read model !
It is impossible de regroup `product_order` and `product_margin` updates in a same transaction, due to async... 

Fortunately, we can make the `ProductMarginUpdater` event consumption conditional to the `product_order`/`product_inventory` update transaction commit, by using the `@TransactionalEventListener(phase = AFTER_COMMIT)` instead of `@EventListener` on the subscriber:
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

Make the test `should_not_update_best_sales_when_order_is_refused_due_to_low_inventory()` pass green, using the `@TransactionalEventListener` in the subscriber.
