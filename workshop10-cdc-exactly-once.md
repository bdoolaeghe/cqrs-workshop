# Workshop 10: Debezium and product_margin update exactly once

_Goal:_ *Fix the previous solution with exactly once event consuming*

# Create a table to register already consumed event
To avoid consuming twice the same event after a `Main` daemon crash/restart, we are going to save a "hash" of every event after its consumption.

Create a `consumed_event` table:
``` 
CREATE TABLE consumed_event
(
    hash TEXT PRIMARY KEY
);
```
Then, implement the `EventDAO`:
``` 
public interface EventDAO {
    boolean exists(String eventHash);
    void insert(String eventHash);
}
```
*NB: `insert(eventHash)` should raise a `DuplicateKeyException` when we try to insert  an already consumed event hash.* 

After that, the `EventDAOImplTest` should pass green...

# Fix the ProductMarginUpdater
Now, in transactional methods `onCreateOrderLine()` and `onDeleteOrderLine()`, we should add a new first step to insert into `consumed_event` a "hash" of the current event to make sure it has not been previously already consumed:
* if the event is consumed for the first time, the "hash" will be inserted in the current transaction
* if the event has already been consumed, the insert will raise a `DuplicateKeyException`, and the `product_margin` should not be updated ! 

You can use the following "hash" in `consumed_event`:
``` 
    private String hash(String operation, Struct rowBefore, Struct rowAfter) {
        return operation + "-" +
                rowBefore + "-" +
                rowAfter;
    }
```
*NB: insert into `consumed_event` and update of `product_margin` SHOULD happen in a same transaction to keep coherency.* 

*NB: you can check out the [workshop10_solution](https://github.com/bdoolaeghe/cqrs-workshop/tree/workshop10_solution) branch if you're lazy to implement it !*
Once fixed, duplicate events will be discarded, and you should be able to successfully replay the crash scenario of [workshop9](https://github.com/bdoolaeghe/cqrs-workshop/blob/workshop9/workshop9-cdc-for-product_margin.md#check-the-coherence-of-data) !
