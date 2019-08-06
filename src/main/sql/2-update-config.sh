#!/usr/bin/env bash

set -e

POSTGRESQL_CONF='/var/lib/postgresql/data/postgresql.conf'

# Debezium (wal2json) plugin configuration
echo "# MODULES" >> "$POSTGRESQL_CONF"
echo "shared_preload_libraries = 'decoderbufs,wal2json'" >> "$POSTGRESQL_CONF"
echo "" >> "$POSTGRESQL_CONF"
echo "# REPLICATION" >> "$POSTGRESQL_CONF"
echo "wal_level = logical" >> "$POSTGRESQL_CONF"
### nb de senders = nb de connectors qu'on peut brancher
echo "max_wal_senders = 2" >> "$POSTGRESQL_CONF"
# un slot permet de brancher un connector qui stream
echo "max_replication_slots = 2" >> "$POSTGRESQL_CONF"

psql -U postgres -c "SELECT pg_reload_conf()"
