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

FAQ
===
Q. I clone the repository, but a maven build report some tests failures.
A. The *master* branch contains junit tests, but partly implemented production code behind ! Your job is to make the tests pass green after having implemented the missing production code, described in the workshops...

Q. My IDE shows compilation errros on unexisting getter/Setter, or logger....
A. Lombok is not activated in your IDE. 
* Install the lombok plugin. File > Settings > plugins > marketplace (search lombok and install)
* enable the annotation processing. In IntelliJ, File > settings > Build, execution, deployments > Compiler > Annotation processor > Enable annotation processing