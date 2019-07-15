# Workshop 1: introduction

_Goal:_ 
Given an e-commerce website, 
build a backoffice service returning the "best sales"

## Setup the project
* first of all, you'll need the following tools:
  - JDK11
  - docker & docker-compose
  - your favorite IDE (e.g. [IntelliJ](https://www.jetbrains.com/idea/download/#section=linux))

You can install all of it running the [setup.sh](setup.sh) on Ubuntu/Debian

* checkout the src project:  
```
/> git clone ssh://git@gitlab.soat.fr:60022/bruno.doolaeghe/cqrs-workshop.git
/> cd cqrs-workshop
cqrs-workshop/> git checkout  workshop1
```

* Import the project into your IDE (IntelliiJ)
  - start [IntelliJ](https://www.jetbrains.com/idea/download/#section=linux)
  - `File > Open` and select `cqrs-workshop/pom.xml`, import as a project. 

* make sure you can build the project:
```
cqrs-workshop/> mvn install -DskipTests
```
Or/and in your IDE, check you can compile the project.

* have a look to the [HowTo](HOWTO.md) page for some FAQ. 


## Implement the [getBestSales()](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/blob/master/src/main/java/fr/soat/cqrs/service/backoffice/BackOfficeServiceImpl.java#L28) service
In the checked out e-commerce website (in this sample project, you will find only the Backend part, with no GUI) you will find:
* a Postgres Database, with a [datamodel](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/tree/master/src/main/sql)
* some [DAO] classes (https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/tree/master/src/main/java/fr/soat/cqrs/dao)
* Some java [services](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/tree/master/src/main/java/fr/soat/cqrs/service)
... A classic [n-tier architecture](https://en.wikipedia.org/wiki/Multitier_architecture).

In this backend, you will find the following services:
* [FrontOffice.order(order)](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/blob/master/src/main/java/fr/soat/cqrs/service/front/FrontService.java#L7), invoked by a _customer_ to pass a new order.
* [BackOffice.getOrder(orderId)](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/blob/master/src/main/java/fr/soat/cqrs/service/backoffice/BackOfficeService.java#L8), invoked by the _order preparers_, to bundle the shipments.
* [BackOffice.getBestSales()](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/blob/master/src/main/java/fr/soat/cqrs/service/backoffice/BackOfficeService.java#L10), used by the _big boss_, to display the dashboard of the best sales ever !

The first and second one are already implemented. Your job now is to implement the [getBestSales()](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/blob/master/src/main/java/fr/soat/cqrs/service/backoffice/BackOfficeServiceImpl.java#L28), returning the 3 best sold products, 
that is, for which we had the biggest total margin:
* the margin per order line is _(product.price - procuct.supply_price) x quantity_
* the total margin for a product is the sum of product margin on all orders.
* the best sales are the products having the highest total margin.

_N.B. : The service test [should_find_best_sales()](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/blob/master/src/test/java/fr/soat/cqrs/service/backoffice/BackOfficeServiceImplTest.java#L41) is allready implemented, but it fails. Once you've implemented the service [getBestSales()](https://gitlab.soat.fr/bruno.doolaeghe/cqrs-workshop/blob/master/src/main/java/fr/soat/cqrs/service/backoffice/BackOfficeServiceImpl.java#L28), the test should pass green !!!_

_Hint: to compute the 3 best sales, you can use the following SQL query:_
```
SELECT product.name as product_name, 
SUM((price - supply_price) * quantity) AS product_margin
FROM product JOIN order_line ON product.reference = order_line.reference
GROUP BY product.name
ORDER BY product_margin DESC
LIMIT 3
```

