INSERT INTO product (reference, name, price, supply_price)
VALUES (1, 'Chaussettes spiderman', 2.9, 1.9),
       (2, 't-shirt bob l eponge', 5.9, 1.9),
       (3, 'robe reine des neiges', 10.9, 8.9);

INSERT INTO product_order(id)
VALUES (101),
       (102),
       (103);

INSERT INTO order_line(id, order_id, reference, quantity)
VALUES (1001, 101, 1, 2),
       (1002, 101, 2, 1),
       (1003, 102, 1, 1),
       (1004, 103, 3, 10);
