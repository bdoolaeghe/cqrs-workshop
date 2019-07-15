HowTo
=====
* Build du projet
```
cqrs-workshop/> mvn install -DskipTests
```
* Lancer les tests
```
cqrs-workshop/> mvn test
```
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
* Lancer un CLI sql (psql) :
```
cqrs-workshop/> make db/psql
```
