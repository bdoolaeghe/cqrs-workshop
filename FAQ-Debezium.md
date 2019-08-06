FAQ - Debezium
==============

# Why is SNAPSHOT initial load running at each startup of the application ?
Check that your `/tmp/debezium/offset-*.dat` files have not been deleted (they contain the state of what data change has already been captured and "commited" in the "backing storage". 
If the files have been deleted, that means there is no previously saved offset, and debezium starts an *initial load*.

# what are the offset-**history.dat files in `/tmp/debezium/` ?
They contain the consumed events offset concerning the DB schema changes (whereas the `offset**.dat` files contain the offset of what *data* changes has already been captured)

# why is there no `offset-*.dat` file in my /tmp/debezium/ ?
The `offset-*.dat` file is written periodically. May be your update period is too long. (check the config key `offset.flush.interval.ms`)

# Got the error "cannot have multiple slots with the same name active for the same database"
If you have the error:
``` 
ERROR io.debezium.connector.postgresql.connection.PostgresReplicationConnection - A logical replication slot named 'my_slot_1' for plugin 'decoderbufs' and database 'postgres' is already active on the server.You cannot have multiple slots with the same name active for the same database
```
You probably have another instance of debezium engine started. Check the running JVMs with `jps` and kill unwanted JVMs.

# I fail to capture events for a given table 
## potential cause 1
Maybe your database state and the offset file have become inconsistent after a postgres container rebuild/restart. delete `/tmp/debezium/` folder content and retry.
## potential cause 2
Check the table *whitelist* in configuration:
``` 
  .with("table.whitelist", tableName)
```
Check the table name (expected FQN table name with *schema*)
To capture changes on all tables, remove the whitelist key.
