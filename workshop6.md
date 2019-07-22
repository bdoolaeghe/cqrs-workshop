# Workshop 6: async event subscriber with ordering guarantee

_Goal:_ 
Guarantee the event consumption order (same as publish order)

## Implement cancelOrder()

First, we want to implement the new feature `FrontService.cancelOrder(orderId)`, to cancel a previously saved order.
This service should:
* delete the order from `product_order` and each order line in `order_line` for the corresponding order
* update `product_margin` read model, to remove the cancelled order margin.

Implement the service `FrontService.cancelOrder()` and `OrderDAO.delete(orderiD)`. This DAO method should raise a `OrderDeletedEvent`, that a new event listener method `onDeletedOrderEvent()` should handle in `ProductMarginUpdater`, to update `product_margin` and decrease the order corresponding margins.
Once implemented, `BackOfficeServiceImplTest.should_decrease_product_margin_when_cancelling_an_order()` should pass green.


## What could possibly go wrong ?
