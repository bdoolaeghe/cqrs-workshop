# Workshop CQRS

Un workshop d'initiation à CQRS

HowTo
=====
* Démarrer postgres et y charger un jeu de données :
```
cqrs-workshop/> make db/up
```
* Stopper postgres :
```
cqrs-workshop/> make db/down
```
* reset postgres (et data) :
```
cqrs-workshop/> make db/reset
```
* Lancer un CLI cql (psql) :
```
cqrs-workshop/> make db/psql
```

TODO
====
cf [trello](https://trello.com/b/QlNUNO3K/formation-cqrs-es)

* monter une DB
* schema.sql
* script d'alim de produits
* docker-compose.yml qui start une base + cree schema + feed produits

SANS CQRS
=========
* service java d'enregistrement d'une commande (FO client)
* service de consultation d'une commande (BO preparateurs)
* service de lecture des meilleures ventes (BO BI)

scenario de test:
* given j'ai passé 3 orders (order = commande, sans ambiguité)
* when je passe une commande de 1000 x chaussettes
* Then chaussettes devient meilleure vente

AVEC CQRS
=========
* intro d'un command handler
* update le service d'enregistrement de commande
* update service de lecteur des meilleures ventes
