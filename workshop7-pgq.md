# Workshop 7: async event subscriber with persisted events

_Goal:_ 
Guarantee consistency with persisting events.


## The problem

The previous solution is quite good, but has some remaining inconsistency issues: 
what would happen in the following scenario:
* the publisher save an order, and commit in the `product_order` table.
* the commit triggers the consumption of an `OrderSavedEvent` by the `ProductMarginUpdater` event listener.
* however, the event listener fails to consume the event (maybe because of a DB Connection lost, or an `OutOtMemoryError`)
The event is considered as "acknowledged", but the database is now inconsistent, and there is nothing to "repair" the data !

## A solution
To keep consistency, a solution is to make "atomic" the data update in DB and event acknowledge. 
One way of doing that is to use DB transactions:
* publisher side, save the `product_order` update and corresponding `OrderSavedEvent` event in a same DB transaction.
* consumer side, save the `product_margin` update and  corresponding `OrderSavedEvent` event acknowledgement in a same DB transaction.
That way, the event and `product_margin` "state" keeps consistent ! 

### Implementation

As a simple solution , we'll save events in a new `order_event` table:
```
CREATE TABLE order_event
(
    event_id            SERIAL PRIMARY KEY,
    event_type          text NOT NULL,
    product_order       text NOT NULL
);
```
*N.B.: the `product_order` column will contain a json serialized view of the `Order` object. You can use the `OrderJsonMapper` to ser/deser an `Order` in json*

#### OrderEventDAO

Then write the implementation of the `OrderEventDAO`:
```
public interface OrderEventDAO {
    void pushOrderEvent(OrderEvent event);
    OrderEvent popOrderEvent();
}
```
*Hint: you can use the following SQL queries:*
* push a new order (enqueue an event):
```
INSERT INTO order_event (event_type, product_order)
VALUES (?,?)
```
* pop the first order (dequeue the first event):
```
DELETE FROM order_event
WHERE event_id = (SELECT MIN(event_id) FROM order_event)
RETURNING event_id, event_type, product_order;
```
*N.B.: your DAO implement should make test `OrderEventDAOImplTest` pass green !

#### Amend OrderDAOImpl
* save an event when saving/deleting an order in `product_order`

#### Amend ProductMarginUpdater
* poll and consume events from `order_event`

### A good solution ?

What are the drawbacks/issues ot storing events in a DB table ? 
* issues due to polling strategy
  - workload on DB
  - workload on network
* issues due to DB technology
  - volume downgrading performance
  - need purge of table
  - locks issues between read and write
  - hard to scale (#consumers in parallel)
 
Some of these issues could be solved using "Queue" DB objects. cf. for example the Postgres Queues ([PGQ](https://wiki.postgresql.org/wiki/PGQ_Tutorial))
However, [PGQ](https://wiki.postgresql.org/wiki/PGQ_Tutorial) remains acceptable for "low" volumes applications (i.e. in practice, most of applications ;-))