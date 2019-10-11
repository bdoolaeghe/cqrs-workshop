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
* Afficher les logs de la DB :
```
cqrs-workshop/> make db/log
```

FAQ
===
## I clone the repository, but a maven build report some tests failures.
The *master* branch contains junit tests, but partly implemented production code behind ! Your job is to make the tests pass green after having implemented the missing production code, described in the workshops...

## My IDE shows compilation errros on unexisting getter/Setter, constructor, logger....
Lombok is not activated in your IDE. 
* Install the lombok plugin. File > Settings > plugins > marketplace (search lombok and install)
* enable the annotation processing. In IntelliJ, File > settings > Build, execution, deployments > Compiler > Annotation processor > Enable annotation processing

# make db/up fail with following message:
```
ERROR: for my_postgres  Cannot create container for service postgres: Conflict. The container name "/my_postgres" is already in use by container "5c813c29ec0f5beb0cf034b78a1496b313d7ae3bb1384aff5659151e64f1970f". You have to remove (or rename) that container to be able to reuse that name.
```
Try the following work aournd and retry:
```
cqrs-workshop/> docker stop my_postgres
cqrs-workshop/> docker rm my_postgres
cqrs-workshop/> make db/up
```
