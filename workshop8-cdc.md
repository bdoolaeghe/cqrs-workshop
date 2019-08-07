# Workshop 8: event queue from change data capture (CDC)

_Goal:_ 
Experiment an event oriented architecture on a legacy DB based application, using the change data capture.

# Understand Debezium and CDC
[Debeizium](https://debezium.io/) is a CDC engine compliant with postgres. 
In this worksop, we are going to use it in embedded mode (light engine, not scalable, no HA but more simple stack).
The embedded mode is working as following:
* *postgres server side:* a postgres WAL (transaction log) consumer plugin, exposing the DB change events on a slot.
* *applicaiton side*: a connector plugged onto the slot, consuming the events.
* events are technical records containing the data row before change, and the data row after change.

First of all, start the postgres server with already setup debezium plugins:
``` 
cqrs-workshop$ make db/reset
```
Than, start the [Main](src/main/java/fr/soat/cqrs/Main.java) spring application, set up as a daemon to listen to and log SourceRecrds (events) from the slot.

Now, open a `psql` CLI:
``` 
cqrs-workshop$ make db/psql
```
And check `Main` logs after trying the following sample queries:
``` 
INSERT INTO product (name, price, supply_price)
VALUES ('casquette pat patrouille', 2.9, 1.9);
UPDATE product set name = 'LA casquette pat patrouille' where name = 'casquette pat patrouille';
DELETE FROM product where name = 'LA casquette pat patrouille';
``` 

*NB: you can have a look to the embedded [debizium engine documentation](https://debezium.io/docs/embedded/#in_the_code) and [postgres connector](https://debezium.io/docs/connectors/postgresql) for more details*

# Implement a new data projection (`order_report`)
We now want to create a new *orders reporting* in a CQRS way using Debezium.
The goal is to be able to report from the new table `order_report`, the last ordering date for each sold product, with its price:

|product_reference|product_name          |price|last_order_date|
|-----------------|----------------------|-----|---------------|
| 2               |t-shirt bob l eponge  | 5.9 |  2019/09/11   |
| ...             |        ...           | ... |  ...          |   

*NB: we consider that a product that has been ordered, then order has been cancelled, should stay in `order_report`.*

To feed `order_report` table, we'ell use [DatabaseChangeEventListener](src/main/java/fr/soat/cqrs/service/backoffice/DatabaseChangeEventListener.java) plugged onto `order_line` table, to insert into `order_report` table whan an order is registered.
In `OrderReportUpdater`, add a listener to capture changes in table `order_line`, using callback `onOrderLineRecord()`:
``` 
public class OrderReportUpdater {
    ...
    public void start() {
        databaseChangeEventListener.startListener("public.order_line", this::onOrderLineRecord);
        log.info(this.getClass().getSimpleName() + " is started (start consuming events)");
    }
    ...
 }
```
Then implement callback method `onOrderLineRecord`:
1. parse the record to react on snapshot/insert operations
2. get the product *reference* from the record
3. load the product by *reference* using `ProductDAO`
4. upsert a row in `order_report` with product reference, name, price, and current Date (Use [OrderReportDAO#upsert()](src/main/java/fr/soat/cqrs/dao/OrderReportDAO.java#L7))

Once implemented, `BackOfficeServiceImplTest` should pass green !
