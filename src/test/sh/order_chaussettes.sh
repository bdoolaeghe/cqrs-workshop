#!/usr/bin/env bash
docker exec -ti my_postgres bash -c "psql -U postgres -f /sql/order_chaussettes.sql"
