.PHONY: all build

all: db/up

db/up:
	docker-compose -f docker-compose.yml up -d

db/down:
	docker-compose down

db/clean:
	docker-compose rm --force

db/psql:
	docker exec -ti cqrsworkshop_postgres_1 bash -c "psql -U postgres"

