INSERT INTO product_order VALUES (nextval('product_order_id_seq'));
INSERT INTO order_line VALUES (nextval('order_line_id_seq'), currval('product_order_id_seq'), 1, 1);
