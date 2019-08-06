.PHONY: all build

all: db/up

db/build:
	docker-compose build

db/up: db/build
	docker-compose -f docker-compose.yml up -d

db/down:
	docker-compose down

db/log:
	docker-compose logs

db/reset: db/down db/up

db/psql:
	docker exec -ti my_postgres bash -c "psql -U postgres"

db/bash:
	docker exec -ti my_postgres bash

