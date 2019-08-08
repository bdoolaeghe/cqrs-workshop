-- total margin sur les chaussettes
SELECT SUM((price - supply_price) * quantity) AS product_margin
FROM product JOIN order_line ON product.reference = order_line.reference
WHERE product.reference = 1;
