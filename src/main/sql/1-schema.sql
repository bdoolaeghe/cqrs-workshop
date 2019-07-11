CREATE TABLE product
(
    reference    SERIAL PRIMARY KEY,
    name         text,
    price        float,
    supply_price float
);
CREATE SEQUENCE product_seq START 4;

CREATE TABLE product_order
(
    id SERIAL PRIMARY KEY
);
CREATE SEQUENCE order_seq START 104;

CREATE TABLE order_line
(
    id        SERIAL PRIMARY KEY,
    order_id  SERIAL REFERENCES product_order(id),
    reference SERIAL REFERENCES product(reference),
    quantity  int NOT NULL default 1
);
CREATE SEQUENCE line_seq START 1004;
