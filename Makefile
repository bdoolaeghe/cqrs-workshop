.PHONY: all build

all: db/up

db/up:
	docker-compose -f docker-compose.yml up -d

db/down:
	docker-compose down

db/log:
	docker-compose logs

db/reset: db/down db/up

db/psql:
	docker exec -ti my__postgres bash -c "psql -U postgres"

