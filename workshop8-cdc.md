# Workshop 8: event queue from change data capture (CDC)

_Goal:_ 
Experiment an event oriented architecture on a legacy state based application, using the change data capture.

# Understand Debezium and CDC
[Debeizium](https://debezium.io/) is a CDC engine compliant with postgres. 
In this worksop, we are going to use it in embedded mode (light engine, not scalable, no HA but more simple stack).
The embedded mode is working as following:
* *postgres server side:* a postgres WAL (transaction log) consumer plugin, exposing the DB change events on a slot.
* *applicaiton side*: a connector plugged onto the slot, consuming the events.
* events are technical records containing the data row before change, and the data row after change.

First of all, start a postgres server with already setup debezium plugins:
``` 
cqrs-workshop$ make db/up
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
