# Workshop 2: getBestSales() in a CQRS way

_Goal:_ 
Given the previous workshop1 e-commerce website, 
build again the backoffice service returning the "best sales", but in a CQRS way this time:
* the *per product total margin* should be persisted in a new `product_margin` table
* table `product_margin` should be updated each time a new order is saved in `product_order` table
* `getBestSales()` should now read from table `product_margin`

## Create the product_margin table
We want to keep the *per product total margin* up to date when we save a new order. Let's create the new `product_margin` table:
```
CREATE TABLE product_margin
(
    product_reference INTEGER PRIMARY KEY,
    product_name      text,
    total_margin      FLOAT
);
 
```
To automatically create the table when running postgres in docker, add the table creation script into `src/main/sql/1-schema.sql` (the scripts in `src/main/sql/` are executed at container init). Now, reset the database:
```
cqrs-workshop/> make db/reset 
```
Check the postgres logs:
```
cqrs-workshop/> make db/reset 
```


## Amend the FrontService.order() service

The `FrontService.order()` should now execute 3 operations:
* save the new order (and each order line) in `product_order`/`product_line` tables (as it already did)
* per order line, compute the new *margin* to append for each bought product
* update the *per product total margin* in `product_margin` table !

### Data consistency
These operations should be executed in a *same transaction* to keep data consistency. To define the transaction in a declarative way, use the `@Transactional` spring annotation on the sercvice method:
```
@Service
public class FrontServiceImpl implements FrontService {
    ...
    @Transactional
    public Long order(Order order) {
        // 1. insert order with OrderDao
        // 2. compute the margin for each product form the order
        // 3. update order products margin with ProductMarginDao
    }
    ...
}
```

### Margin computation
To update the total margin for each order product, you will have to compute in java the *per product margin* for each line of the order:
```
  margin on product = (product.price - product.supply_price) x order.quantity
```

### Update the total margins in DB 
For each product, the computed margin should be accumulated in `product_margin` table (add to the previous `total_margin` value for the product reference). 

Create a new `ProductMarginDAO` with a method `void incrementProductMargin(Long productReference, String productName, float marginToAdd)` 
To "increment" the `product_margin.total_margin` column in one request, you can use the following UPDATE query:
```
UPDATE product_margin
SET total_margin = total_margin + {the new computed margin for the order product}
WHERE product_reference = ?
```
N.B.: you will also have to handle the case where the product is bought for the first time (no row yet to update in `product_margin` !). In this case, we should insert the computed margin as `total_margin` value:
```
INSERT INTO product_margin (product_reference, product_name, total_margin) 
VALUES (?, ?, ?)";
```

## Amend the getBestSales() service 

Last but not least, update the `getBestSales()` DAO method to read the best sales from the new `product_margin` table:
```
SELECT product_name,
       total_margin as product_margin
FROM product_margin
ORDER BY total_margin DESC
LIMIT 3
``` 

Check the [should_find_best_sales()](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/blob/workshop1_solution/src/test/java/fr/soat/cqrs/service/backoffice/BackOfficeServiceImplTest.java#L30) test is green... Voilà !
