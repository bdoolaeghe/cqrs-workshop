.PHONY: all build

all: db/up

db/build:
	mkdir -p /tmp/debezium
	docker-compose build

db/up: db/build
	docker-compose -f docker-compose.yml up -d

db/down:
	docker-compose down

db/log:
	docker-compose logs

db/reset: db/down db/clean db/up

db/clean:
	docker volume rm postgresql-data-95 || exit 0
	rm /tmp/debezium/offset*.dat || exit 0

db/psql:
	docker exec -ti my_postgres bash -c "psql -U postgres"

db/bash:
	docker exec -ti my_postgres bash

