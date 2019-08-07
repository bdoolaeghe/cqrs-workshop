# Workshop 9: Debezium and product_margin update

_Goal:_ 
Enhance the previous event oriented architecture using the change data capture to update product_margin.



then listen to:
* insert/delete/update on order_lines => enough to update the product_margin 

deal with duplicate (at least once)

and third : big boss alerting

# Implement the "big boss alerting"
remove me ?

NB: initial load aka "snapshot"
NB2: how to manage aggregates ? (order = order_lines)
