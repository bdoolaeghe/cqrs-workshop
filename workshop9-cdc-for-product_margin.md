# Workshop 9: Debezium and product_margin update

_Goal:_ *test the data coherence with Debezium CDC*

# Implement product_margin update with Debezium
Like in workshop8, use Debezium to update `product_margin` like we did for `order_report`.
`BackOfficeServiceImplTest` should then pass green !

*NB: you can skip this step by checking out `workshop9_solution` to access the second part of this workshop.*

# Check the coherence of data
The solution is working fine, but, let's check the data coherency after a *Debezium* listener crash... 
Debezium is supposed to save its current position in data captured event queue with the offset backing file `/tmp/debezium/offset-*.dat`, so that, if the daemon is down when something is written in the DB, the corresponding event should be queued, and consumed by Debezium when the daemon is restarted.
Let's check the daemon will fail over:
1. start the `Main` daemon, which should capture changes on `order_line` and update `product_margin`.
2. now, let's insert an order in DB (for convenience, you can run [order_chaussettes.sh](src/test/sh/order_chaussettes.sh) to insert an order of *chaussettes spiderman* in quantity = 1).
   The exepcted *total_margin* in `product_margin` is:
   ``` 
   total_margin = SUM((price - supply_price) x quantity)
                = (2.9 - 1.9) x 1 = 1
   ```
   You can compute that with the workshop1 query:
   ``` 
   -- total margin sur les chaussettes
   SELECT SUM((price - supply_price) * quantity) AS product_margin
   FROM product JOIN order_line ON product.reference = order_line.reference
   WHERE product.reference = 1;
   ```
   And check the `product_margin` content:
   ``` 
   -- total margin sur les cuassettes
   SELECT *
   FROM product_margin
   WHERE product_reference = 1;
   ```
3. Now, stop the `Main` daemon (stop the JVM or kill it), as if we had a crash of it.
4. After that, insert a second identical order in DB for *chaussettes spiderman* (use [order_chaussettes.sh](src/test/sh/order_chaussettes.sh)).
5. Restart the `Main` daemon. We expect it will use its offset backing file `/tmp/debezium/offset-*.dat` to capture the missed second order when it was stopped.
6. Compute again the total margin and compare with `product_margin` value. We expect a *total_margin*:
   ``` 
   total_margin = SUM((price - supply_price) x quantity)
                = (2.9 - 1.9) x 1 + (2.9 - 1.9) x 1 = 2

   ```
   All right ?
